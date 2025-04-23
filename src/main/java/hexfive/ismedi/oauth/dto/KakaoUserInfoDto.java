package hexfive.ismedi.oauth.dto;

import hexfive.ismedi.domain.User;
import hexfive.ismedi.oauth.AuthService;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;

@Getter
@Builder
public class KakaoUserInfoDto {
    private String email;
    private String name;
    private User.Gender gender;
    LocalDate birthday;
}
