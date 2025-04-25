package hexfive.ismedi.users;

import hexfive.ismedi.users.dto.UserResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class UserService {
    private final UserRepository userRepository;

    public UserResponseDto findById(Long id){
        return userRepository.findById(id)
                .map(UserResponseDto::fromEntity)
                .orElseThrow(() -> new RuntimeException("사용자 없음"));
    }
}
