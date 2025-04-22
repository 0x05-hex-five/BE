package hexfive.ismedi.category;

import hexfive.ismedi.category.dto.CreateCategoryDto;
import hexfive.ismedi.category.dto.ResCategoryDto;
import hexfive.ismedi.category.dto.UpdateCategoryDto;
import hexfive.ismedi.global.exception.CustomException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

import static hexfive.ismedi.global.exception.ErrorCode.CATEGORY_NOT_FOUND;

@Service
@RequiredArgsConstructor
public class CategoryService {
    private final CategoryRepository categoryRepository;

    public ResCategoryDto createCategory(CreateCategoryDto createCategoryDto) {
        Category category = createCategoryDto.toEntity();
        categoryRepository.save(category);
        return ResCategoryDto.fromEntity(category);
    }

    public ResCategoryDto getCategory(Long id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new CustomException(CATEGORY_NOT_FOUND));
        return ResCategoryDto.fromEntity(category);
    }

    public List<ResCategoryDto> getAllCategories() {
        return categoryRepository.findAll()
                .stream()
                .map(ResCategoryDto::fromEntity)
                .collect(Collectors.toList());
    }

    public ResCategoryDto updateCategory(Long id, UpdateCategoryDto updateCategoryDto) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new CustomException(CATEGORY_NOT_FOUND));
        category.update(updateCategoryDto.getDisplayName(), updateCategoryDto.getSelectName());
        return ResCategoryDto.fromEntity(category);
    }

    public void deleteCategory(Long id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new CustomException(CATEGORY_NOT_FOUND));
        categoryRepository.delete(category);
    }
}
