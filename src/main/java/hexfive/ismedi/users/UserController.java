package hexfive.ismedi.users;


import hexfive.ismedi.global.response.APIResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users")
@SecurityRequirement(name = "JWT")
public class UserController {

    @Operation(
            summary = "토큰 정상 응답 테스트",
            description = "Access Token을 헤더에 담아 전송했을 경우 정상응답 테스트하는 API입니다.",
            security = @SecurityRequirement(name = "JWT")
    )
    @GetMapping("/test")
    public ResponseEntity<?> test(){

        return ResponseEntity.ok(APIResponse.success("test ok"));
    }
}
