package com.ecomlab.ecommerce.service.impl;

import com.ecomlab.ecommerce.dto.request.CategoryRequest;
import com.ecomlab.ecommerce.dto.response.CategoryResponse;
import com.ecomlab.ecommerce.entity.CategoryEntity;
import com.ecomlab.ecommerce.exception.BusinessException;
import com.ecomlab.ecommerce.repository.CategoryRepository;
import com.ecomlab.ecommerce.service.CategoryService;
import com.ecomlab.ecommerce.service.builder.CategoryResponseBuilder;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {
  private final CategoryRepository categoryRepository;

  @Override
  @Transactional(readOnly = true)
  public List<CategoryResponse> categories() {
    List<CategoryEntity> categories = categoryRepository.findAllActive();

    return categories.stream().map(CategoryResponseBuilder::build).toList();
  }

  @Override
  @Transactional
  public CategoryResponse create(CategoryRequest request) {
    CategoryEntity category = new CategoryEntity();
    apply(category, request);
    return CategoryResponseBuilder.build(categoryRepository.save(category));
  }

  @Override
  @Transactional
  public CategoryResponse update(UUID id, CategoryRequest request) {
    CategoryEntity category = category(id);
    apply(category, request);
    return CategoryResponseBuilder.build(category);
  }

  @Override
  @Transactional
  public void delete(UUID id) {
    category(id).softDelete();
  }

  private CategoryEntity category(UUID id) {
    return categoryRepository
        .findByIdAndIsDeletedFalse(id)
        .orElseThrow(
            () ->
                new BusinessException(
                    "CATEGORY_NOT_FOUND", "Category not found", HttpStatus.NOT_FOUND));
  }

  private void apply(CategoryEntity category, CategoryRequest request) {
    category.setParent(request.parentId() == null ? null : category(request.parentId()));
    category.setName(request.name());
    category.setDescription(request.description());
  }
}
