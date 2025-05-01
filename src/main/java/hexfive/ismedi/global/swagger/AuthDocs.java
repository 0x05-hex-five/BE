package hexfive.ismedi.global.swagger;

import hexfive.ismedi.global.response.APIResponse;
import hexfive.ismedi.jwt.TokenDto;
import hexfive.ismedi.oauth.dto.SignupRequestDto;
import hexfive.ismedi.users.dto.KaKaoLoginResultDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.view.RedirectView;

public interface AuthDocs {

    @Operation(summary = "웹 카카오 로그인 요청", description = "카카오 인증 서버로 리다이렉트합니다.")
    @GetMapping("/login")
    RedirectView kakaoLogin();

    @Operation(
            summary = "앱 카카오 로그인 요청",
            description = "앱에서 발급받은 카카오 access_token을 이용해 로그인합니다. 신규 유저일 경우 isNew=true로 응답됩니다."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "기존 유저 or 신규 유저 구분 응답"),
            @ApiResponse(responseCode = "400", description = "[INVALID_TOKEN] 유효하지 않은 카카오 토큰"),
            @ApiResponse(responseCode = "403", description = "토큰 인증 실패")
    })
    @Parameter(hidden = true)
    @PostMapping("/login/app")
    APIResponse<KaKaoLoginResultDto> kakaoAppLogin(@RequestHeader("Authorization") String header);

    @Operation(summary = "카카오 로그인 콜백", description = "인가 코드를 처리하여 사용자 정보를 조회합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "기존 유저 or 신규 유저 구분 응답"),
            @ApiResponse(responseCode = "400", description = "토큰 발급 또는 사용자 정보 조회 실패")
    })
    @GetMapping("/login/kakao/callback")
    APIResponse<KaKaoLoginResultDto> kakaoCallback(@RequestParam String code);

    @Operation(summary = "Access Token 재발급",
            description = "Refresh Token을 검증한 후 Access Token을 재발급합니다.",
            security = @SecurityRequirement(name = "JWT"))
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "토큰 재발급 성공"),
            @ApiResponse(responseCode = "401", description = "[INVALID_TOKEN] 유효하지 않은 토큰입니다."),
            @ApiResponse(responseCode = "401", description = "[TOKEN_EXPIRED] 토큰이 만료되었습니다.")
    })
    @Parameter(hidden = true)
    @PostMapping("/reissue")
    APIResponse<TokenDto> reissueToken(@RequestHeader("Authorization") String header);

    @Operation(summary = "로그아웃", description = "Access Token을 열어보고 회원의 Refresh Token을 삭제합니다.",
            security = @SecurityRequirement(name = "JWT"))
    @PostMapping("/logout")
    APIResponse<String> logout(@RequestHeader("Authorization") String header);

    @Operation(summary = "회원가입", description = "카카오 사용자 정보 및 추가 정보를 통해 회원가입을 진행합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "회원가입 성공 및 토큰 발급"),
            @ApiResponse(responseCode = "400", description = "입력값 유효성 검사 실패 또는 중복 이메일")
    })
    @PostMapping("/signup")
    APIResponse<?> signup(@RequestBody SignupRequestDto request);
}