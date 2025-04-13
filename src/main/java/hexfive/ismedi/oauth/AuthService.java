package hexfive.ismedi.oauth;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import hexfive.ismedi.domain.User;
import hexfive.ismedi.jwt.TokenDto;
import hexfive.ismedi.oauth.dto.KakaoUserInfoDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
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

import static hexfive.ismedi.domain.User.Gender.MAN;
import static hexfive.ismedi.domain.User.Gender.WOMAN;

@Slf4j
@Service
@RequiredArgsConstructor
@RestController
public class AuthService {
    private final ObjectMapper objectMapper;

    @Value("${kakao.client-id}")
    private String clientId;

    @Value("${kakao.redirect-uri}")
    private String redirectUri;

    @Value("${kakao.client-secret}")
    private String clientSecret;

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

        /*
        * Authorization : "Authorization"이라는 이름의 HTTP 헤더를 추가
        * Bearer : OAuth 2.0에서 access token을 쓸 때 사용하는 토큰 타입
        * */
        httpHeaders.set("Authorization", "Bearer " + accessToken);

        /*
        * Content-Type: application/x-www-form-urlencoded
        * POST 요청 시, body가 키=값&키=값 형식으로 전송된다는 뜻
        * 이런 헤더 정보는 카카오 로그인 API 명세에 나와있음
        * */
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
        /*
            /v2/user/me 응답 format
            {
                "id": 123456789,
                "kakao_account": {
                    "email": "user@example.com",
                    "profile": {
                        "nickname": "홍길동"
                    },
                    "name": "홍길동",
                    "gender": "male",
                    "birthday": "0720",
                    "phone_number": "+82 10-1234-5678"
                }
            }
        */

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
}
