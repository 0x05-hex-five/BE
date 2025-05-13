package hexfive.ismedi.global.swagger;

import hexfive.ismedi.global.response.APIResponse;
import hexfive.ismedi.users.dto.FCMTokenRequestDto;
import hexfive.ismedi.users.dto.UpdateRequestDto;
import hexfive.ismedi.users.dto.UserResponseDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@SecurityRequirement(name = "JWT")
public interface UserDocs {

    @Operation(summary = "회원 정보 조회", description = "로그인한 사용자의 정보를 조회합니다. 다른 사용자의 ID를 요청하면 403 에러가 발생합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "회원 정보 조회 성공"),
            @ApiResponse(responseCode = "403", description = "본인 외 다른 사용자의 정보에 접근하려는 경우"),
            @ApiResponse(responseCode = "404", description = "존재하지 않는 사용자 ID")
    })
    @GetMapping("/{id}")
    APIResponse<UserResponseDto> getUserInfo(
            @Parameter(description = "조회할 사용자 ID", example = "10") @PathVariable Long id,
            @AuthenticationPrincipal UserDetails userDetails
    );

    @Operation(summary = "회원 정보 수정", description = "로그인한 사용자의 정보를 수정합니다. 다른 사용자의 ID를 수정하려는 경우 403 에러가 발생합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "회원 정보 수정 성공"),
            @ApiResponse(responseCode = "400", description = "요청 필드에 유효하지 않은 값이 있는 경우"),
            @ApiResponse(responseCode = "403", description = "본인 외 다른 사용자의 정보에 접근하려는 경우"),
            @ApiResponse(responseCode = "404", description = "존재하지 않는 사용자 ID")
    })
    @PatchMapping("/{id}")
    APIResponse<UserResponseDto> updateUserInfo(
            @Parameter(description = "수정할 사용자 ID", example = "10") @PathVariable Long id,
            @Valid @RequestBody UpdateRequestDto updateRequestDto,
            @AuthenticationPrincipal UserDetails userDetails
    );

    @Operation(summary = "회원 탈퇴", description = "로그인한 사용자의 계정을 삭제합니다. 다른 사용자의 ID로 요청 시 403 에러가 발생합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "회원 탈퇴 성공"),
            @ApiResponse(responseCode = "403", description = "본인 외 다른 사용자의 정보에 접근하려는 경우"),
            @ApiResponse(responseCode = "404", description = "존재하지 않는 사용자 ID")
    })
    @DeleteMapping("/{id}")
    APIResponse<?> deleteUserInfo(
            @Parameter(description = "삭제할 사용자 ID", example = "10") @PathVariable Long id,
            @AuthenticationPrincipal UserDetails userDetails
    );


    @Operation(
            summary = "FCM 토큰 등록",
            description = "모바일 앱에서 발급된 FCM 토큰을 서버에 등록합니다. 이 토큰을 통해 서버가 푸시 알림을 전송할 수 있습니다."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "토큰 등록 성공"),
            @ApiResponse(responseCode = "404", description = "존재하지 않는 사용자 ID")
    })
    @PostMapping("/fcm-token")
    APIResponse<Void> getToken(
            @AuthenticationPrincipal @Parameter(hidden = true) UserDetails userDetails,
            @RequestBody FCMTokenRequestDto fcmtokenRequestDto
    );
}
