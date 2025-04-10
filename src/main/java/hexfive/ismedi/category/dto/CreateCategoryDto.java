package hexfive.ismedi.category.dto;

import hexfive.ismedi.category.Category;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class CreateCategoryDto {

    @NotNull(message = "표시 이름은 null일 수 없습니다.")
    @NotBlank(message = "표시 이름은 필수입니다.")
    private String displayName;

    @NotNull(message = "선택 이름은 null일 수 없습니다.")
    @NotBlank(message = "선택 이름은 필수입니다.")
    private String selectName;

    public Category toEntity() {
        return Category.builder()
                .displayName(this.displayName)
                .selectName(this.selectName)
                .build();
    }
}