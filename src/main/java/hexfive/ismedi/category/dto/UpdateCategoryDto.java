package hexfive.ismedi.category.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class UpdateCategoryDto {
    @NotNull(message = "표시 이름은 null일 수 없습니다.")
    @NotBlank(message = "표시 이름은 필수입니다.")
    private String displayName;

    @NotNull(message = "선택 이름은 null일 수 없습니다.")
    @NotBlank(message = "선택 이름은 필수입니다.")
    private String selectName;
}