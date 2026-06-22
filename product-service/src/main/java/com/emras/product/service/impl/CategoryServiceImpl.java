package com.emras.product.service.impl;
import com.emras.product.constant.CacheKeys;
import com.emras.product.constant.ErrorMessages;
import com.emras.product.dto.request.CreateCategoryRequest;
import com.emras.product.dto.response.CategoryResponse;
import com.emras.product.entity.Category;
import com.emras.product.exception.CategoryNotFoundException;
import com.emras.product.mapper.CategoryMapper;
import com.emras.product.repository.CategoryRepository;
import com.emras.product.service.CategoryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {
    private final CategoryRepository categoryRepository;
    private final CategoryMapper      categoryMapper;
    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "categories", key = "'all'")
    public List<CategoryResponse> getAllCategories() {
        return categoryMapper.toResponseList(
                categoryRepository.findAllTopLevelWithChildren());
    }
    @Override
    @Transactional(readOnly = true)
    public CategoryResponse getCategoryById(Long id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new CategoryNotFoundException(ErrorMessages.CATEGORY_NOT_FOUND));
        return categoryMapper.toResponse(category);
    }
    @Override
    @Transactional
    @CacheEvict(value = "categories", allEntries = true)
    public CategoryResponse createCategory(CreateCategoryRequest request) {
        Category category = Category.builder()
                .nameEn(request.nameEn())
                .nameBn(request.nameBn())
                .slug(request.slug())
                .imageUrl(request.imageUrl())
                .build();

        if (request.parentId() != null) {
            Category parent = categoryRepository.findById(request.parentId())
                    .orElseThrow(() -> new CategoryNotFoundException(ErrorMessages.CATEGORY_NOT_FOUND));
            category.setParent(parent);
        }

        Category saved = categoryRepository.save(category);
        log.info("Category created: id={} slug={}", saved.getId(), saved.getSlug());
        return categoryMapper.toResponse(saved);
    }
    @Override
    @Transactional
    @CacheEvict(value = "categories", allEntries = true)
    public CategoryResponse updateCategory(Long id, CreateCategoryRequest request) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new CategoryNotFoundException(ErrorMessages.CATEGORY_NOT_FOUND));

        category.setNameEn(request.nameEn());
        category.setNameBn(request.nameBn());
        category.setSlug(request.slug());
        category.setImageUrl(request.imageUrl());

        return categoryMapper.toResponse(categoryRepository.save(category));
    }
    @Override
    @Transactional
    @CacheEvict(value = "categories", allEntries = true)
    public void deleteCategory(Long id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new CategoryNotFoundException(ErrorMessages.CATEGORY_NOT_FOUND));
        category.setActive(false);
        categoryRepository.save(category);
    }
}