package hexfive.ismedi.oauth;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import hexfive.ismedi.domain.User;
import hexfive.ismedi.exception.DuplicateEmailException;
import hexfive.ismedi.jwt.JwtProvider;
import hexfive.ismedi.jwt.TokenDto;
import hexfive.ismedi.oauth.dto.KakaoUserInfoDto;
import hexfive.ismedi.oauth.dto.SignupRequestDto;
import hexfive.ismedi.oauth.dto.SignupResponseDto;
import hexfive.ismedi.users.UserRepository;
import hexfive.ismedi.users.dto.KaKaoLoginResultDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.util.Map;
import java.util.Optional;

import static hexfive.ismedi.domain.User.Gender.MAN;
import static hexfive.ismedi.domain.User.Gender.WOMAN;

@Slf4j
@Service
@RequiredArgsConstructor
@RestController
public class AuthService {
    private final ObjectMapper objectMapper;
    private final UserRepository userRepository;
    private final JwtProvider jwtProvider;

    @Value("${kakao.client-id}")
    private String clientId;

    @Value("${kakao.redirect-uri}")
    private String redirectUri;

    @Value("${kakao.client-secret}")
    private String clientSecret;

    public KaKaoLoginResultDto kakaoLogin(String code) {
        String kakaoAccessToken = getAccessToken(code);

        KakaoUserInfoDto userInfo = getUserInfoByAccessToken(kakaoAccessToken);

        KaKaoLoginResultDto result = loginOrJoin(userInfo);

        // 신규 회원
        if (result.isNew()) {
            return KaKaoLoginResultDto.builder()
                    .isNew(true)
                    .userInfo(result.getUserInfo())
                    .build();
        }
        // 기존 회원
        User user = userRepository.findByEmail(userInfo.getEmail())
                .orElseThrow(() -> new IllegalArgumentException("사용자 없음"));

        String accessToken = jwtProvider.generateAccessToken(user.getId());
        String refreshToken = jwtProvider.generateRefreshToken(user.getId());

        TokenDto tokenDto = TokenDto.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .userId(user.getId())
                .build();

        return KaKaoLoginResultDto.builder()
                .isNew(false)
                .userInfo(result.getUserInfo())
                .token(tokenDto)
                .build();
    }

    // Controller에서 넘겨받은 code값으로 카카오 서버에 토큰 요청하는 메서드
    public String getAccessToken(String code){
        RestTemplate restTemplate = new RestTemplate(); // 스프링 내장 클래스 for REST API

        // 헤더 설정
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        // 바디 파라미터 설정
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("grant_type", "authorization_code");
        params.add("client_id", clientId);
        params.add("redirect_uri", redirectUri);
        params.add("code", code);
        params.add("client_secret", clientSecret);

        // 요청 객체 생성
        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(params, httpHeaders);

        // 요청 전송
        ResponseEntity<String> response = restTemplate.postForEntity(
                "https://kauth.kakao.com/oauth/token",
                request,
                String.class
        );

        // 응답에서 access_token 파싱
        try {
            TokenDto tokenDto = objectMapper.readValue(response.getBody(), TokenDto.class);
            return tokenDto.getAccessToken();
        } catch (Exception e) {
            log.error("카카오 액세스 토큰 요청 실패", e);
            throw new RuntimeException("카카오 로그인 중 오류가 발생했습니다.");
        }
    }

    // 토큰을 담아 카카오 서버에 요청하고 사용자 정보를 받아오는 메서드
    public KakaoUserInfoDto getUserInfoByAccessToken(String accessToken){
        // 헤더 설정
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.set("Authorization", "Bearer " + accessToken);
        httpHeaders.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        // 요청 객체 생성
        HttpEntity<?> request = new HttpEntity<>(httpHeaders);
        // 요청 전송
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<String> response = restTemplate.postForEntity(
                "https://kapi.kakao.com/v2/user/me",
                request,
                String.class
        );

        // json 파싱
        try{
            JsonNode jsonNode = objectMapper.readTree(response.getBody());
            String email = jsonNode.get("kakao_account").get("email").asText();
            String name = jsonNode.get("kakao_account").get("name").asText();
            String kakaoGender = jsonNode.get("kakao_account").get("gender").asText();
            User.Gender gender = fromKakaoGender(kakaoGender);
            String birthDay = jsonNode.get("kakao_account").get("birthday").asText();
            String birthYear = jsonNode.get("kakao_account").get("birthyear").asText();
            String fullBirthday = birthYear + "-" + birthDay.substring(0, 2) + "-" + birthDay.substring(2, 4);
            LocalDate birthDate = LocalDate.parse(fullBirthday);

            return KakaoUserInfoDto.builder()
                    .email(email)
                    .name(name)
                    .birthday(birthDate)
                    .gender(gender)
                    .build();
        } catch (Exception e) {
            log.error("카카오 사용자 정보 파싱 실패", e);
            throw new RuntimeException("사용자 정보 파싱 실패: " + e.getMessage());
        }
    }

    public static User.Gender fromKakaoGender(String kakaoGender) {
        if (kakaoGender == null) return null;

        return switch (kakaoGender.toLowerCase()) {
            case "female" -> WOMAN;
            case "male" -> MAN;
            default -> throw new IllegalArgumentException("지원하지 않는 성별 값입니다: " + kakaoGender);
        };
    }

    // 전달받은 사용자의 데이터를 확인 후 로그인 / 회원가입 분기처리 하는 메서드
    public KaKaoLoginResultDto loginOrJoin(KakaoUserInfoDto kakaoUserInfoDto){
        // 이메일로 회원 조회
        Optional<User> optionalUser = userRepository.findByEmail(kakaoUserInfoDto.getEmail());

        // 기존 회원
        if (optionalUser.isPresent()) {
            return KaKaoLoginResultDto.builder()
                    .isNew(false)
                    .userInfo(kakaoUserInfoDto)
                    .build();
        }
        // 신규 회원
        return KaKaoLoginResultDto.builder()
                .isNew(true)
                .userInfo(kakaoUserInfoDto)
                .build();
    }

    public TokenDto reissueAccessToken(String refreshToken) {
        return jwtProvider.reissueAccessToken(refreshToken);
    }

    public boolean logout(String AccessToken) {
        jwtProvider.addToBlacklist(AccessToken);
        return jwtProvider.deleteRefreshToken(AccessToken);
    }

    public Map<String, Object> signup(SignupRequestDto request) {
        // 1. 이메일 중복 체크
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new DuplicateEmailException("이미 가입된 이메일입니다.");
        }

        // 2. User 엔티티 생성 및 저장
        User user = User.builder()
                .name(request.getName())
                .email(request.getEmail())
                .birth(request.getBirth())
                .gender(request.getGender())
                .pregnant(request.isPregnant())
                .alert(request.isAlert())
                .build();

        try {
            User savedUser = userRepository.save(user);

            // 3. JWT 토큰 발급
            String accessToken = jwtProvider.generateAccessToken(savedUser.getId());
            String refreshToken = jwtProvider.generateRefreshToken(savedUser.getId());

            // 4. 응답용 DTO 조립
            TokenDto token = TokenDto.builder()
                    .accessToken(accessToken)
                    .refreshToken(refreshToken)
                    .userId(savedUser.getId())
                    .build();

            SignupResponseDto userInfo = SignupResponseDto.builder()
                    .id(savedUser.getId())
                    .name(savedUser.getName())
                    .email(savedUser.getEmail())
                    .build();

            // 5. 응답용 데이터 Map으로 묶어 반환
            return Map.of("token", token, "userInfo", userInfo);

        } catch (DataIntegrityViolationException e) {
            throw new DuplicateEmailException("이미 가입된 이메일입니다.");
        }
    }
}
