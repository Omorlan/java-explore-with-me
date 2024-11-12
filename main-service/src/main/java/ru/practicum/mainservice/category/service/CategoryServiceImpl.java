package ru.practicum.mainservice.category.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.mainservice.category.dto.CategoryDto;
import ru.practicum.mainservice.category.dto.NewCategoryDto;
import ru.practicum.mainservice.category.mapper.CategoryMapper;
import ru.practicum.mainservice.category.model.Category;
import ru.practicum.mainservice.category.repository.CategoryRepository;
import ru.practicum.mainservice.exception.exception.NotFoundException;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CategoryServiceImpl implements CategoryService {
    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;

    @Override
    @Transactional
    public CategoryDto create(NewCategoryDto newCategoryDto) {
        log.info("Creating category with name={}", newCategoryDto.getName());
        Category category = categoryRepository.save(categoryMapper.fromNewCategoryDto(newCategoryDto));
        return categoryMapper.toCategoryDto(category);
    }

    @Override
    @Transactional
    public void delete(Long catId) {
        log.info("Deleting category with id={}", catId);
        categoryRepository.delete(findCategoryByIdOrThrow(catId));
    }

    @Override
    @Transactional
    public CategoryDto update(Long catId, CategoryDto categoryDto) {
        log.info("Updating category with id={}", catId);
        Category category = findCategoryByIdOrThrow(catId);
        category.setName(categoryDto.getName());
        return categoryMapper.toCategoryDto(categoryRepository.save(category));
    }

    @Override
    public List<CategoryDto> getCategories(int offset, int size) {
        Pageable pageable = PageRequest.of(offset / size, size);
        return categoryRepository.findAll(pageable)
                .getContent()
                .stream()
                .map(categoryMapper::toCategoryDto)
                .toList();
    }

    @Override
    public CategoryDto getCategoryById(Long catId) {
        log.info("Fetching category with id={}", catId);
        return categoryMapper.toCategoryDto(findCategoryByIdOrThrow(catId));
    }

    private Category findCategoryByIdOrThrow(Long catId) {
        return categoryRepository.findById(catId)
                .orElseThrow(() -> new NotFoundException(String.format("Category with id=%d was not found.", catId)));
    }
}
