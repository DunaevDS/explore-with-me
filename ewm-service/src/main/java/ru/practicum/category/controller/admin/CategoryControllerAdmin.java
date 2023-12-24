package ru.practicum.category.controller.admin;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.category.dto.CategoryDto;
import ru.practicum.category.dto.NewCategoryDto;
import ru.practicum.category.service.CategoryService;

import javax.validation.Valid;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/admin/categories")
public class CategoryControllerAdmin {

    private final CategoryService categoryService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CategoryDto addCategory(@Valid @RequestBody NewCategoryDto categoryDto) {
        log.info(String.format("Получен запрос POST /admin/categories на добавление новой категории %s",
                categoryDto.getName()));
        return categoryService.addCategory(categoryDto);
    }

    @DeleteMapping(path = "/{catId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void removeCategory(@PathVariable Long catId) {
        log.info(String.format("Получен запрос DELETE /admin/categories/{catId} = %s на удаление категории",
                catId));
        categoryService.removeCategory(catId);
    }

    @PatchMapping(path = "/{catId}")
    public CategoryDto updateCategory(@PathVariable Long catId,
                                      @Valid @RequestBody CategoryDto categoryDto) {
        log.info(String.format("Получен запрос PATCH /admin/categories/{catId}=%s на изменение категории",
                catId));
        return categoryService.updateCategory(catId, categoryDto);
    }
}
