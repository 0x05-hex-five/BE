package hexfive.ismedi.global.swagger;

import hexfive.ismedi.global.response.APIResponse;
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
    APIResponse<UserResponseDto> getUserInfo(
            @Parameter(hidden = true) UserDetails userDetails
    );

    @Operation(summary = "회원 정보 수정", description = "로그인한 사용자의 정보를 수정합니다. 다른 사용자의 ID를 수정하려는 경우 403 에러가 발생합니다.")
    APIResponse<UserResponseDto> updateUserInfo(
            @Valid @RequestBody UpdateRequestDto updateRequestDto,
            @Parameter(hidden = true) UserDetails userDetails
    );

    @Operation(summary = "회원 탈퇴", description = "로그인한 사용자의 계정을 삭제합니다. 다른 사용자의 ID로 요청 시 403 에러가 발생합니다.")
    APIResponse<?> deleteUserInfo(
            @Parameter(hidden = true) UserDetails userDetails
    );
}
