package com.ecomlab.ecommerce.service.builder;

import com.ecomlab.ecommerce.dto.response.CategoryResponse;
import com.ecomlab.ecommerce.entity.CategoryEntity;

public final class CategoryResponseBuilder {
  private CategoryResponseBuilder() {}

  public static CategoryResponse build(CategoryEntity category) {
    return CategoryResponse.builder()
        .id(category.getId())
        .parentId(category.getParent() == null ? null : category.getParent().getId())
        .name(category.getName())
        .description(category.getDescription())
        .build();
  }
}
