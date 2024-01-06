package ru.practicum.category.controller.pub;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.category.dto.CategoryDto;
import ru.practicum.category.dto.CategoryMapper;
import ru.practicum.category.service.CategoryService;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/categories")
public class CategoryControllerPublic {

    private final CategoryService categoryService;

    @GetMapping
    public List<CategoryDto> getCategories(@RequestParam(required = false, defaultValue = "0") Integer from,
                                           @RequestParam(required = false, defaultValue = "10") Integer size) {
        log.info(String.format("Получен запрос GET /categories на получение списка категорий с параметрами " +
                "from = %s, size = %s", from, size));
        return categoryService.getCategories(from, size);
    }

    @GetMapping(path = "/{catId}")
    public CategoryDto getCategoryById(@PathVariable Long catId) {
        log.info(String.format("Получен запрос GET /categories/{catId} = %s на получение категории", catId));
        return CategoryMapper.toCategoryDto(categoryService.getCategoryById(catId));
    }
}
