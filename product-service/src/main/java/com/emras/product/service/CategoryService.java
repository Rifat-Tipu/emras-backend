package com.emras.product.service;
import com.emras.product.dto.request.CreateCategoryRequest;
import com.emras.product.dto.response.CategoryResponse;
import java.util.List;

public interface CategoryService {
    List<CategoryResponse> getAllCategories();
    CategoryResponse getCategoryById(Long id);
    CategoryResponse createCategory(CreateCategoryRequest request);
    CategoryResponse updateCategory(Long id, CreateCategoryRequest request);
    void deleteCategory(Long id);
}