package ru.practicum.category.dto;


import lombok.experimental.UtilityClass;
import ru.practicum.category.model.Category;

import java.util.ArrayList;
import java.util.List;

@UtilityClass
public class CategoryMapper {

    public Category toCategory(NewCategoryDto categoryDto) {
        return new Category(
                null,
                categoryDto.getName()
        );
    }

    public CategoryDto toCategoryDto(Category category) {
        return new CategoryDto(
                category.getId(),
                category.getName()
        );
    }

    public List<CategoryDto> toDtos(List<Category> categories) {
        List<CategoryDto> dtos = new ArrayList<>();
        for (Category category : categories) {
            dtos.add(toCategoryDto(category));
        }
        return dtos;
    }
}
