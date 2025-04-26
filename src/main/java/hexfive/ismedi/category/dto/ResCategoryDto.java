package hexfive.ismedi.category.dto;

import hexfive.ismedi.category.Category;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ResCategoryDto {
    private Long id;
    private String displayName;
    private String selectName;

    public static ResCategoryDto fromEntity(Category category) {
        return ResCategoryDto.builder()
                .id(category.getId())
                .displayName(category.getDisplayName())
                .selectName(category.getSelectName())
                .build();
    }
}