package hexfive.ismedi.category;

import hexfive.ismedi.category.dto.CreateCategoryDto;
import hexfive.ismedi.category.dto.ResCategoryDto;
import hexfive.ismedi.category.dto.UpdateCategoryDto;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/categories")
@RequiredArgsConstructor
public class CategoryController {
    private final CategoryService categoryService;

    @PostMapping
    public ResCategoryDto createCategory(@RequestBody CreateCategoryDto createCategoryDto) {
        return categoryService.createCategory(createCategoryDto);
    }

    @GetMapping("/{id}")
    public ResCategoryDto getCategory(@PathVariable Long id) {
        return categoryService.getCategory(id);
    }

    @GetMapping
    public List<ResCategoryDto> getAllCategories() {
        return categoryService.getAllCategories();
    }

    @PutMapping("/{id}")
    public ResCategoryDto updateCategory(@PathVariable Long id, @RequestBody UpdateCategoryDto updateCategoryDto) {
        return categoryService.updateCategory(id, updateCategoryDto);
    }

    @DeleteMapping("/{id}")
    public void deleteCategory(@PathVariable Long id) {
        categoryService.deleteCategory(id);
    }
}
