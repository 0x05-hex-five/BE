package hexfive.ismedi.users.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
public class UpdateRequestDto {
    @NotNull(message = "생년월일은 null일 수 없습니다.")
    @NotBlank(message = "생년월일은 필수입니다.")
    private Date birth;

    @NotNull(message = "성별은 null일 수 없습니다.")
    @NotBlank(message = "성별은 필수입니다.")
    private String gender;

    private Boolean pregnant;
    private Boolean alert;
}
