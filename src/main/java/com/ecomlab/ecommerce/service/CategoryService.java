package com.ecomlab.ecommerce.service;

import com.ecomlab.ecommerce.dto.request.CategoryRequest;
import com.ecomlab.ecommerce.dto.response.CategoryResponse;
import java.util.List;
import java.util.UUID;

public interface CategoryService {
  List<CategoryResponse> categories();

  CategoryResponse create(CategoryRequest request);

  CategoryResponse update(UUID id, CategoryRequest request);

  void delete(UUID id);
}
