package com.uiwave.ecommerce.category.service;

import java.util.List;

import com.uiwave.ecommerce.catalog.dto.CategoryRequest;
import com.uiwave.ecommerce.catalog.dto.CategoryResponse;

public interface CategoryService {

    List<CategoryResponse> listAll();

    CategoryResponse create(CategoryRequest request);

    CategoryResponse update(Long id, CategoryRequest request);

    void delete(Long id);

}
