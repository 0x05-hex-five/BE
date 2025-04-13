package hexfive.ismedi.users;

import hexfive.ismedi.domain.User;
import hexfive.ismedi.oauth.dto.KakaoUserInfoDto;
import hexfive.ismedi.users.dto.KaKaoLoginResultDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@RequiredArgsConstructor
@Service
public class UserService {
    private final UserRepository userRepository;
    
    // Auth 컨트롤러에서 전달받은 사용자의 데이터를 확인 후 로그인 / 회원가입 분기처리 하는 메서드
    public KaKaoLoginResultDto loginOrJoin(KakaoUserInfoDto kakaoUserInfoDto){
        // 이메일로 회원 조회
        Optional<User> optionalUser = userRepository.findByEmail(kakaoUserInfoDto.getEmail());

        // 기존 회원
        if (optionalUser.isPresent()) {
            return KaKaoLoginResultDto.builder()
                    .isNew(false)
                    .userInfo(kakaoUserInfoDto)
                    .build();
        }
        // 신규 회원
        return KaKaoLoginResultDto.builder()
                .isNew(true)
                .userInfo(kakaoUserInfoDto)
                .build();
    }
}
