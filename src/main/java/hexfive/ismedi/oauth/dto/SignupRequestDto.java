package hexfive.ismedi.oauth.dto;

import hexfive.ismedi.domain.User.Gender;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.Date;

@Getter
@Setter
@Schema(description = "회원가입 요청 DTO")
public class SignupRequestDto {

    @NotBlank(message = "이름은 필수입니다.")
    @Schema(description = "사용자 이름", example = "홍길동")
    private String name;

    @Email(message = "이메일 형식이 아닙니다.")
    @NotBlank(message = "이메일은 필수입니다.")
    @Schema(description = "사용자 이메일", example = "hong@example.com")
    private String email;

    @NotNull(message = "생년월일은 필수입니다.")
    @Schema(description = "생년월일", example = "2000-01-01")
    private Date birth;

    @NotNull(message = "성별은 필수입니다.")
    @Schema(description = "성별 (WOMAN 또는 MAN)", example = "WOMAN")
    private Gender gender;

    @Schema(description = "임신 여부", example = "false")
    private boolean pregnant;

    @Schema(description = "알림 수신 여부", example = "true")
    private boolean alert;
}
