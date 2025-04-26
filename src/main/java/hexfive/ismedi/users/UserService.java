package hexfive.ismedi.users;

import hexfive.ismedi.domain.User;
import hexfive.ismedi.global.exception.CustomException;
import hexfive.ismedi.users.dto.UpdateRequestDto;
import hexfive.ismedi.users.dto.UserResponseDto;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import static hexfive.ismedi.global.exception.ErrorCode.USER_NOT_FOUND;

@RequiredArgsConstructor
@Service
public class UserService {
    private final UserRepository userRepository;

    public UserResponseDto getUserInfo(Long id){
        return userRepository.findById(id)
                .map(UserResponseDto::fromEntity)
                .orElseThrow(() -> new CustomException(USER_NOT_FOUND));
    }

    @Transactional
    public UserResponseDto updateUserInfo(Long id, UpdateRequestDto updateRequestDto){
        User user = userRepository.findById(id)
                .orElseThrow(() -> new CustomException(USER_NOT_FOUND));
        user.updateUserInfo(
                updateRequestDto.getBirth(),
                User.Gender.valueOf(updateRequestDto.getGender()),
                updateRequestDto.getPregnant(),
                updateRequestDto.getAlert()
        );
        return UserResponseDto.fromEntity(user);
    }

    public void deleteUserInfo(Long id){
        User user = userRepository.findById(id)
                .orElseThrow(() -> new CustomException(USER_NOT_FOUND));
        userRepository.delete(user);
    }
}
