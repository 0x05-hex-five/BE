package hexfive.ismedi.oauth;

import hexfive.ismedi.domain.User;
import hexfive.ismedi.global.APIResponse;
import hexfive.ismedi.global.ErrorCode;
import hexfive.ismedi.jwt.JwtProvider;
import hexfive.ismedi.jwt.TokenDto;
import hexfive.ismedi.oauth.dto.KakaoUserInfoDto;
import hexfive.ismedi.oauth.dto.SignupRequestDto;
import hexfive.ismedi.oauth.dto.SignupResponseDto;
import hexfive.ismedi.users.UserRepository;
import hexfive.ismedi.users.UserService;
import hexfive.ismedi.users.dto.KaKaoLoginResultDto;
import io.jsonwebtoken.JwtException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.View;
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
    private final View error;

    @GetMapping("/test")
    public ResponseEntity<String> testToken() { return ResponseEntity.status(HttpStatus.OK).body("test ok"); }

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
    @PostMapping("/login/kakao/callback")
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

    @Operation(
            summary = "로그아웃",
            description = """
                Redis에 저장된 Refresh Token을 삭제합니다.
                이 API는 Refresh Token이 필요합니다.
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
        System.out.println(token);
        Boolean logoutSuccess = jwtProvider.deleteRefreshToken(token);

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
    public ResponseEntity<?> signup(@RequestBody @Valid SignupRequestDto request, BindingResult bindingResult){
        // @Valid 결과
        if(bindingResult.hasErrors()){
            String errorMessage = bindingResult.getAllErrors().get(0).getDefaultMessage(); // 자바 21 이상에서는 getFirst() 쓰면 된대
            return ResponseEntity.badRequest().body(APIResponse.fail(null, errorMessage));
        }

        // 중복 이메일 확인
        if(userRepository.existsByEmail(request.getEmail())){
            return ResponseEntity.badRequest().body(APIResponse.fail(
                    null,
                    "이미 가입된 이메일입니다."
            ));
        }
        
        // 회원가입 및 토큰 발급
        try {
            // User 엔티티 생성
            User user = User.builder()
                    .name(request.getName())
                    .email(request.getEmail())
                    .birth(request.getBirth())
                    .gender(request.getGender())
                    .pregnant(request.isPregnant())
                    .alert(request.isAlert())
                    .build();
            
            User savedUser = userRepository.save(user);

            // 토큰 발급
            String accessToken = jwtProvider.generateAccessToken(savedUser.getId());
            String refreshToken = jwtProvider.generateRefreshToken(savedUser.getId());

            // 토큰 DTO
            TokenDto tokenDto = TokenDto.builder()
                    .accessToken(accessToken)
                    .refreshToken(refreshToken)
                    .userId(savedUser.getId())
                    .build();

            // 응답 DTO
            SignupResponseDto userInfo = SignupResponseDto.builder()
                    .id(savedUser.getId())
                    .name(savedUser.getName())
                    .email(savedUser.getEmail())
                    .build();

            return ResponseEntity.ok(APIResponse.success(
                    Map.of(
                            "token", tokenDto,
                            "userInfo", userInfo
                    )
            ));
        } catch (DataIntegrityViolationException e) {
            return ResponseEntity.badRequest().body(APIResponse.fail(null, "이미 가입된 이메일입니다."));
        }
    }
}









