package ru.practicum.category.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.category.dto.CategoryDto;
import ru.practicum.category.dto.NewCategoryDto;
import ru.practicum.category.repository.CategoryRepository;
import ru.practicum.exception.CategoryNotFoundException;
import ru.practicum.category.dto.CategoryMapper;
import ru.practicum.category.model.Category;
import ru.practicum.exception.DataValidationException;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;

    @Override
    @Transactional
    public CategoryDto addCategory(NewCategoryDto newCategoryDto) {
        Category category = CategoryMapper.toCategory(newCategoryDto);

        log.info("Создание новой категории.");
        return CategoryMapper.toCategoryDto(categoryRepository.save(category));
    }

    @Override
    @Transactional
    public void removeCategory(Long categoryId) {
        if (!categoryRepository.existsById(categoryId)) {
            throw new DataValidationException("Не существует категории с выбранным id");
        }
        categoryRepository.deleteById(categoryId);
        log.info("Категория с id {} удалена", categoryId);
    }

    @Override
    @Transactional
    public CategoryDto updateCategory(Long categoryId, CategoryDto categoryDto) {
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new CategoryNotFoundException("Категории с выбранным id не существует"));
        category.setName(categoryDto.getName());
        log.info("Обновление категории с id {}", categoryId);
        return CategoryMapper.toCategoryDto(categoryRepository.save(category));
    }

    @Override
    public List<CategoryDto> getCategories(Integer from, Integer size) {
        log.info("Выполняется запрос на получение категорий");
        Pageable pageable = PageRequest.of(from / size, size);
        List<Category> categories = categoryRepository.findAll(pageable).getContent();
        return CategoryMapper.toDtos(categories);
    }

    @Override
    public Category getCategoryById(Long categoryId) {
        log.info("Выполняется поиск категории в базе по id {}", categoryId);
        return categoryRepository.findById(categoryId)
                .orElseThrow(() -> new CategoryNotFoundException("Категории с выбранным id не существует"));
    }
}
