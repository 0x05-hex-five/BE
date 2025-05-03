package hexfive.ismedi.users;


import hexfive.ismedi.global.response.APIResponse;
import hexfive.ismedi.global.swagger.UserControllerDocs;
import hexfive.ismedi.users.dto.UpdateRequestDto;
import hexfive.ismedi.users.dto.UserResponseDto;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users")
@SecurityRequirement(name = "JWT")
public class UserController implements UserControllerDocs {
    private final UserService userService;

    @GetMapping("/{id}")
    public APIResponse<UserResponseDto> getUserInfo(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails userDetails
    ){
        Long loginUserId = Long.parseLong(userDetails.getUsername());
        return APIResponse.success(userService.getUserInfo(loginUserId, id));
    }

    @PatchMapping("/{id}")
    public APIResponse<UserResponseDto> updateUserInfo(
            @PathVariable Long id,
            @Valid @RequestBody UpdateRequestDto updateRequestDto,
            @AuthenticationPrincipal UserDetails userDetails
    ){
        Long loginUserId = Long.parseLong(userDetails.getUsername());
        return APIResponse.success(userService.updateUserInfo(loginUserId, id, updateRequestDto));
    }

    @DeleteMapping("/{id}")
    public APIResponse<?> deleteUserInfo(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails userDetails
    ){
        Long loginUserId = Long.parseLong(userDetails.getUsername());
        userService.deleteUserInfo(loginUserId, id);
        return APIResponse.success(null);
    }
}
