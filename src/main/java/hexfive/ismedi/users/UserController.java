package hexfive.ismedi.users;


import hexfive.ismedi.favorites.FavoriteService;
import hexfive.ismedi.global.response.APIResponse;
import hexfive.ismedi.global.swagger.UserDocs;
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
public class UserController implements UserDocs {
    private final UserService userService;
    private final FavoriteService favoriteService;

    @GetMapping
    public APIResponse<UserResponseDto> getUserInfo(
            @AuthenticationPrincipal UserDetails userDetails
    ){
        Long loginUserId = Long.parseLong(userDetails.getUsername());
        return APIResponse.success(userService.getUserInfo(loginUserId));
    }

    @PatchMapping
    public APIResponse<UserResponseDto> updateUserInfo(
            @Valid @RequestBody UpdateRequestDto updateRequestDto,
            @AuthenticationPrincipal UserDetails userDetails
    ){
        Long loginUserId = Long.parseLong(userDetails.getUsername());
        return APIResponse.success(userService.updateUserInfo(loginUserId, updateRequestDto));
    }

    @DeleteMapping
    public APIResponse<String> deleteUserInfo(
            @AuthenticationPrincipal UserDetails userDetails
    ){
        Long loginUserId = Long.parseLong(userDetails.getUsername());
        userService.deleteUserInfo(loginUserId);
        favoriteService.removeAllFavorites(loginUserId);
        return APIResponse.success("회원 정보가 삭제되었습니다.");
    }
}
