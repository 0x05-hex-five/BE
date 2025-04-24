package hexfive.ismedi.oauth;

import hexfive.ismedi.global.response.APIResponse;
import hexfive.ismedi.global.exception.CustomException;
import hexfive.ismedi.global.swagger.AuthControllerDocs;
import hexfive.ismedi.jwt.TokenDto;
import hexfive.ismedi.oauth.dto.KakaoUserInfoDto;
import hexfive.ismedi.oauth.dto.SignupRequestDto;
import hexfive.ismedi.users.dto.KaKaoLoginResultDto;
import io.jsonwebtoken.JwtException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.view.RedirectView;

import java.util.Map;

import static hexfive.ismedi.global.exception.ErrorCode.*;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/auth")
public class AuthController implements AuthControllerDocs {
    private final AuthService authService;

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

    @PostMapping("/login/app")
    public APIResponse<KaKaoLoginResultDto> kakaoAppLogin(@RequestHeader("Authorization") String header){
        String accessToken = header.substring(7);
        return APIResponse.success(authService.kakaoLoginByAccessToken(accessToken));
    }

    @GetMapping("/login/kakao/callback")
    public APIResponse<KaKaoLoginResultDto> kakaoCallback(@RequestParam String code){
        return APIResponse.success(authService.kakaoLoginByAuthorizationCode(code));
    }

    @PostMapping("/reissue")
    public APIResponse<TokenDto> reissueToken(@RequestHeader("Authorization") String header){
        if (header == null || !header.startsWith("Bearer ")) {
            throw new CustomException(MISSING_TOKEN);
        }

        String refreshToken = header.substring(7);
        try {
            TokenDto newToken = authService.reissueAccessToken(refreshToken);
            return APIResponse.success(newToken);
        } catch (JwtException e) {
            throw new CustomException(INVALID_TOKEN);
        }
    }

    @PostMapping("/logout")
    public APIResponse<String> logout(@RequestHeader("Authorization") String header) {
        if (header == null || !header.startsWith("Bearer ")) {
            throw new CustomException(MISSING_TOKEN);
        }

        String token = header.substring(7);
        boolean logoutSuccess = authService.logout(token);

        if(!logoutSuccess){
            throw new CustomException(LOGOUT_FAILED);
        }
        return APIResponse.success("로그아웃 되었습니다.");
    }

    @PostMapping("/signup")
    public APIResponse<?> signup(@RequestBody @Valid SignupRequestDto request){
        Map<String, Object> result = authService.signup(request);
        return APIResponse.success(result);
    }
}
