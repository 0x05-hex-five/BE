package hexfive.ismedi.category.dto;

import hexfive.ismedi.domain.Category;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ResCategoryDto {
    private Long id;
    private String displayName;
    private String selectName;

    @Builder
    public ResCategoryDto(Long id, String displayName, String selectName) {
        this.id = id;
        this.displayName = displayName;
        this.selectName = selectName;
    }

    public static ResCategoryDto fromEntity(Category category) {
        return ResCategoryDto.builder()
                .id(category.getId())
                .displayName(category.getDisplayName())
                .selectName(category.getSelectName())
                .build();
    }
}