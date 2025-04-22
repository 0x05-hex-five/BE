package hexfive.ismedi.oauth;

import hexfive.ismedi.global.APIResponse;
import hexfive.ismedi.global.ErrorCode;
import hexfive.ismedi.jwt.TokenDto;
import hexfive.ismedi.oauth.dto.SignupRequestDto;
import hexfive.ismedi.users.dto.KaKaoLoginResultDto;
import io.jsonwebtoken.JwtException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
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
    private final AuthService authService;

    @Value("${kakao.client-id}")
    private String clientId;

    @Value("${kakao.redirect-uri}")
    private String redirectUri;

    @Operation(
            summary = "카카오 로그인 요청",
            description = "카카오 인증 서버로 리다이렉트합니다."
    )
    @GetMapping("/login")
    public RedirectView kakaoLogin() {
        String kakaoAuthUrl = "https://kauth.kakao.com/oauth/authorize"
                + "?client_id=" + clientId
                + "&redirect_uri=" + redirectUri
                + "&response_type=code";

        return new RedirectView(kakaoAuthUrl);
    }

    @Operation(
            summary = "카카오 로그인 콜백",
            description = """
                카카오에서 리다이렉트된 인가 코드를 처리하여 사용자 정보를 조회합니다.
            """
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "기존 유저 or 신규 유저 구분 응답"),
            @ApiResponse(responseCode = "400", description = "토큰 발급 또는 사용자 정보 조회 실패")
    })
    @GetMapping("/login/kakao/callback")
    public ResponseEntity<?> kakaoCallback(@RequestParam String code){
        try {
            KaKaoLoginResultDto result = authService.kakaoLogin(code);
            return ResponseEntity.ok(APIResponse.success(result));
        } catch (Exception e) {
            ErrorCode errorCode = ErrorCode.INTERNAL_ERROR;
            return ResponseEntity.status(errorCode.getStatus())
                    .body(APIResponse.fail(
                            Map.of(
                                    "code", errorCode.getCode(),
                                    "status", errorCode.getStatus()
                            ),
                            e.getMessage()
                    ));
        }

    }

    @Operation(
            summary = "Access Token 재발급",
            description = """
                Refresh Token을 검증한 후 Access Token을 재발급합니다.
                이 API는 Refresh Token이 필요합니다.
            """,
            security = @SecurityRequirement(name = "JWT")
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "토큰 재발급 성공"),
            @ApiResponse(responseCode = "401", description = "[INVALID_TOKEN] 유효하지 않은 토큰입니다."),
            @ApiResponse(responseCode = "401", description = "[TOKEN_EXPIRED] 토큰이 만료되었습니다.")
    })
    @Parameter(hidden = true)
    @PostMapping("/reissue")
    public ResponseEntity<?> reissueToken(@RequestHeader("Authorization") String header){
        if (header == null || !header.startsWith("Bearer ")) {
            ErrorCode errorCode = ErrorCode.MISSING_TOKEN;
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(APIResponse.fail(
                            Map.of(
                                    "code", errorCode.getCode(),
                                    "status", errorCode.getStatus()
                            ),
                            errorCode.getMessage()
                    ));
        }

        String refreshToken = header.substring(7);

        try {
            TokenDto newToken = authService.reissueAccessToken(refreshToken);
            return ResponseEntity.ok(APIResponse.success(newToken));
        } catch (JwtException e) {
            ErrorCode errorCode = ErrorCode.INVALID_TOKEN;
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(APIResponse.fail(
                            Map.of(
                                "code", errorCode.getCode(),
                                "status", errorCode.getStatus()
                            ),
                            e.getMessage()
                    ));
        }
    }

    @Operation(
            summary = "로그아웃",
            description = """
                Access Token을 열어보고, 해당 회원의 userId를 조회해 Redis에 저장된 Refresh Token을 삭제합니다.
                이 API는 Access Token이 필요합니다.
            """,
            security = @SecurityRequirement(name = "JWT")
    )
    @PostMapping("/logout")
    public ResponseEntity<?> logout(@RequestHeader("Authorization") String header) {
        if (header == null || !header.startsWith("Bearer ")) {
            ErrorCode errorCode = ErrorCode.MISSING_TOKEN;
            return ResponseEntity.badRequest().body(APIResponse.fail(
                    Map.of(
                            "code", errorCode.getCode(),
                            "status", errorCode.getStatus()
                    ),
                    errorCode.getMessage()
            ));
        }

        String token = header.substring(7);
        boolean logoutSuccess = authService.logout(token);

        if(!logoutSuccess){
            ErrorCode errorCode = ErrorCode.LOGOUT_FAILED;
            return ResponseEntity.ok(APIResponse.fail(
                    Map.of(
                            "code", errorCode.getCode(),
                            "status", errorCode.getStatus()
                    ),
                    errorCode.getMessage()
            ));
        }
        return ResponseEntity.ok(APIResponse.success(
                "로그아웃 되었습니다."
        ));
    }

    @Operation(
            summary = "회원가입",
            description = """
                카카오 사용자 정보 및 추가 정보를 통해 사용자 정보를 등록합니다.
                회원가입 후 Access Token과 Refresh Token이 함께 발급되어 로그인 상태로 전환됩니다.
            """
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "회원가입 성공 및 토큰 발급"),
            @ApiResponse(responseCode = "400", description = "입력값 유효성 검사 실패 또는 중복 이메일")
    })
    @PostMapping("/signup")
    public ResponseEntity<?> signup(@RequestBody @Valid SignupRequestDto request){
        Map<String, Object> result = authService.signup(request);
        return ResponseEntity.ok(APIResponse.success(result));
    }
}









