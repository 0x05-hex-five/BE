package hexfive.ismedi.oauth;

import hexfive.ismedi.domain.User;
import hexfive.ismedi.global.APIResponse;
import hexfive.ismedi.jwt.JwtProvider;
import hexfive.ismedi.jwt.TokenDto;
import hexfive.ismedi.jwt.TokenType;
import hexfive.ismedi.oauth.dto.KakaoUserInfoDto;
import hexfive.ismedi.users.UserRepository;
import hexfive.ismedi.users.UserService;
import hexfive.ismedi.users.dto.KaKaoLoginResultDto;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.view.RedirectView;

import java.util.Map;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;  // 의존성 주입 받기
    private final UserService userService;
    private final UserRepository userRepository;
    private final JwtProvider jwtProvider;
    private final RedisTemplate<String, String> redisTemplate;

    @GetMapping("/test")
    public ResponseEntity<String> testToken() { return ResponseEntity.status(HttpStatus.OK).body("test ok"); }

    @GetMapping("/test/invalid-token")
    public ResponseEntity<?> simulateInvalidToken() {
        throw new RuntimeException("강제로 발생시킨 예외입니다.");
    }

    @Value("${kakao.client-id}")
    private String clientId;

    @Value("${kakao.redirect-uri}")
    private String redirectUri;

    @GetMapping("/login")
    public RedirectView kakaoLogin() {
        String kakaoAuthUrl = "https://kauth.kakao.com/oauth/authorize"
                + "?client_id=" + clientId
                + "&redirect_uri=" + redirectUri
                + "&response_type=code";

        return new RedirectView(kakaoAuthUrl);
    }

    @GetMapping("/login/kakao/callback")
    public ResponseEntity<?> kakaoCallback(@RequestParam String code, HttpServletResponse response){
        //String accessToken = AuthService.getAccessToken(code); -> 의존성 주입받아서 사용해야 함

        String accessToken = authService.getAccessToken(code);
        KakaoUserInfoDto userInfo = authService.getUserInfoByAccessToken(accessToken);
        KaKaoLoginResultDto result = userService.loginOrJoin((userInfo));

        if (result.isNew()) {
            // 신규 회원 -> 추가 정보 입력 페이지로 이동해야 함
            return ResponseEntity.ok(APIResponse.success(
                    KaKaoLoginResultDto.builder()
                            .isNew(true)
                            .userInfo(result.getUserInfo())
                            .build()
            ));
        } else {
            // 기존 회원 -> JWT 토큰 발급
            User user = userRepository.findByEmail(result.getUserInfo().getEmail())
                    .orElseThrow(() -> new IllegalArgumentException("사용자 없음"));
            String jwtAccessToken = jwtProvider.generateAccessToken(user.getId());
            String jwtRefreshToken = jwtProvider.generateRefreshToken(user.getId());

            TokenDto tokenDto = TokenDto.builder()
                    .accessToken(jwtAccessToken)
                    .refreshToken(jwtRefreshToken)
                    .userId(user.getId())
                    .build();

            return ResponseEntity.ok(APIResponse.success(
                    KaKaoLoginResultDto.builder()
                            .isNew(false)
                            .token(tokenDto)
                            .userInfo(result.getUserInfo())
                            .build()
            ));
        }
    }

    @PostMapping("/reissue")
    public ResponseEntity<?> reissueToken(@RequestHeader("Authorization") String header){

        if (header == null || !header.startsWith("Bearer ")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(APIResponse.fail(Map.of("code", "MISSING_TOKEN", "status", 401), "Refresh Token이 없습니다"));
        }

        String refreshToken = header.substring(7); // Bearer 제거

        try {
            TokenDto newToken = jwtProvider.reissueAccessToken(refreshToken);
            return ResponseEntity.ok(APIResponse.success(newToken));
        } catch (JwtException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(APIResponse.fail(
                            Map.of("code", "INVALID_TOKEN", "status", 401),
                            e.getMessage()
                    ));
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(@RequestHeader("Authorization") String header) {
        if (header == null || !header.startsWith("Bearer ")) {
//            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
//                    .body(APIResponse.fail("Access Token이 없습니다."));
        }

        String token = header.substring(7);
        jwtProvider.deleteRefreshToken(token);
        return ResponseEntity.ok(APIResponse.success(
                "로그아웃 되었습니다."
        ));
    }

}









