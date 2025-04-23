package hexfive.ismedi.category;

import hexfive.ismedi.category.dto.CreateCategoryDto;
import hexfive.ismedi.category.dto.ResCategoryDto;
import hexfive.ismedi.category.dto.UpdateCategoryDto;
import hexfive.ismedi.global.response.APIResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/categories")
@RequiredArgsConstructor
public class CategoryController {
    private final CategoryService categoryService;

    @PostMapping
    public APIResponse<ResCategoryDto> createCategory(@Valid @RequestBody CreateCategoryDto createCategoryDto) {
        return APIResponse.success(categoryService.createCategory(createCategoryDto));
    }

    @GetMapping("/{id}")
    public APIResponse<ResCategoryDto> getCategory(@PathVariable Long id) {
        return APIResponse.success(categoryService.getCategory(id));
    }

    @GetMapping
    public APIResponse<List<ResCategoryDto>> getAllCategories() {
        return APIResponse.success(categoryService.getAllCategories());
    }

    @PutMapping("/{id}")
    public APIResponse<ResCategoryDto> updateCategory(@PathVariable Long id, @Valid @RequestBody UpdateCategoryDto updateCategoryDto) {
        return APIResponse.success(categoryService.updateCategory(id, updateCategoryDto));
    }

    @DeleteMapping("/{id}")
    public APIResponse<Void> deleteCategory(@PathVariable Long id) {
        categoryService.deleteCategory(id);
        return APIResponse.success(null);
    }
}