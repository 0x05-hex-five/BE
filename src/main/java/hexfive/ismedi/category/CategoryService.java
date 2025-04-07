package hexfive.ismedi.category;

import hexfive.ismedi.category.dto.CreateCategoryDto;
import hexfive.ismedi.category.dto.ResCategoryDto;
import hexfive.ismedi.category.dto.UpdateCategoryDto;
import hexfive.ismedi.domain.Category;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

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
                .orElseThrow(() -> new IllegalArgumentException("Category not found"));
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
                .orElseThrow(() -> new IllegalArgumentException("Category not found"));
        category.update(updateCategoryDto.getDisplayName(), updateCategoryDto.getSelectName());
        return ResCategoryDto.fromEntity(category);
    }

    public void deleteCategory(Long id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Category not found"));
        categoryRepository.delete(category);
    }
}
