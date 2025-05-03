package hexfive.ismedi.users;

import hexfive.ismedi.domain.User;
import hexfive.ismedi.global.exception.CustomException;
import hexfive.ismedi.users.dto.UpdateRequestDto;
import hexfive.ismedi.users.dto.UserResponseDto;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import static hexfive.ismedi.global.exception.ErrorCode.ACCESS_DENIED;
import static hexfive.ismedi.global.exception.ErrorCode.USER_NOT_FOUND;

@RequiredArgsConstructor
@Service
public class UserService {
    private final UserRepository userRepository;

    public UserResponseDto getUserInfo(Long loginUserId, Long id){
        if (!loginUserId.equals(id)) {
            throw new CustomException(ACCESS_DENIED);
        }
        return userRepository.findById(id)
                .map(UserResponseDto::fromEntity)
                .orElseThrow(() -> new CustomException(USER_NOT_FOUND));
    }

    @Transactional
    public UserResponseDto updateUserInfo(Long loginUserId, Long id, UpdateRequestDto updateRequestDto){
        if (!loginUserId.equals(id)) {
            throw new CustomException(ACCESS_DENIED);
        }
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

    @Transactional
    public void deleteUserInfo(Long loginUserId, Long id){
        if (!loginUserId.equals(id)) {
            throw new CustomException(ACCESS_DENIED);
        }
        User user = userRepository.findById(id)
                .orElseThrow(() -> new CustomException(USER_NOT_FOUND));
        userRepository.delete(user);
    }
}
