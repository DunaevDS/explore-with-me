package ru.practicum.category.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.category.dto.CategoryDto;
import ru.practicum.category.dto.NewCategoryDto;
import ru.practicum.category.service.CategoryService;

import javax.validation.Valid;
import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;

    @PostMapping(value = "admin/categories")
    @ResponseStatus(HttpStatus.CREATED)
    public CategoryDto addCategory(@Valid @RequestBody NewCategoryDto categoryDto) {
        log.info(String.format("Получен запрос POST /admin/categories на добавление новой категории %s",
                categoryDto.getName()));
        return categoryService.addCategory(categoryDto);
    }

    @DeleteMapping(value = "admin/categories/{catId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void removeCategory(@PathVariable Long catId) {
        log.info(String.format("Получен запрос DELETE /admin/categories/{catId} = %s на удаление категории",
                catId));
        categoryService.removeCategory(catId);
    }

    @PatchMapping(value = "admin/categories/{catId}")
    public CategoryDto updateCategory(@PathVariable Long catId,
                                      @Valid @RequestBody CategoryDto categoryDto) {
        log.info(String.format("Получен запрос PATCH /admin/categories/{catId}=%s на изменение категории",
                catId));
        return categoryService.updateCategory(catId, categoryDto);
    }

    @GetMapping("/categories")
    public List<CategoryDto> getCategories(@RequestParam(required = false, defaultValue = "0") Integer from,
                                            @RequestParam(required = false, defaultValue = "10") Integer size) {
        log.info(String.format("Получен запрос GET /categories на получение списка категорий с параметрами " +
                "from = %s, size = %s", from, size));
        return categoryService.getCategories(from, size);
    }

    @GetMapping("categories/{catId}")
    public CategoryDto getCategoryById(@PathVariable Long catId) {
        log.info(String.format("Получен запрос GET /categories/{catId} = %s на получение категории", catId));
        return categoryService.getCategoryById(catId);
    }
}
