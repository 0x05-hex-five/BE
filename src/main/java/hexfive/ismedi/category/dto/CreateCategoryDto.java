package hexfive.ismedi.category.dto;
import hexfive.ismedi.domain.Category;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class CreateCategoryDto {
    private String displayName;
    private String selectName;
    public Category toEntity() {
        return Category.builder()
                .displayName(this.displayName)
                .selectName(this.selectName)
                .build();
    }
}
