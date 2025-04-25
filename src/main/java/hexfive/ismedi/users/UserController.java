package hexfive.ismedi.users;


import hexfive.ismedi.global.response.APIResponse;
import hexfive.ismedi.users.dto.UserResponseDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users")
@SecurityRequirement(name = "JWT")
public class UserController {
    private final UserService userService;

    // GET /api/users/id
    @GetMapping("/{id}")
    public APIResponse<UserResponseDto> findById(@PathVariable Long id){
        return APIResponse.success(userService.findById(id));
    }

    // PATCH /api/users/id
    @PatchMapping("/{id}")
    public APIResponse<?> updateUser(@PathVariable Long id){

        return APIResponse.success("");
    }

    // DELETE /api/users/id
    @DeleteMapping("/{id}")
    public APIResponse<?> deleteUser(@PathVariable Long id){

        return APIResponse.success("");
    }
}
