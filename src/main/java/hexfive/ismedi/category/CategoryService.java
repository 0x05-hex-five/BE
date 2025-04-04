package hexfive.ismedi.category;

import hexfive.ismedi.category.dto.CreateCategoryDto;
import hexfive.ismedi.category.dto.ResCategoryDto;
import hexfive.ismedi.domain.Category;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CategoryService {
    private final CategoryRepository categoryRepository;
    public ResCategoryDto createCategory(CreateCategoryDto createCategoryDto) {
        Category category = createCategoryDto.toEntity();
        categoryRepository.save(category);
        return ResCategoryDto.fromEntity(category);
    }
}
