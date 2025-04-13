package hexfive.ismedi.users;


import hexfive.ismedi.global.APIResponse;
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
public class UserController {

    @GetMapping("/test")
    public ResponseEntity<?> test(){

        return ResponseEntity.ok(APIResponse.success("test ok"));
    }
}
