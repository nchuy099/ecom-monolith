package com.ecomlab.ecommerce.controller;

import com.ecomlab.ecommerce.dto.request.CategoryRequest;
import com.ecomlab.ecommerce.dto.response.CategoryResponse;
import com.ecomlab.ecommerce.service.CategoryService;
import jakarta.validation.Valid;
import java.util.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/categories")
@RequiredArgsConstructor
public class CategoryController {
  private final CategoryService categoryService;

  @GetMapping
  public ResponseEntity<List<CategoryResponse>> list() {
    return ResponseEntity.ok(categoryService.categories());
  }

  @PostMapping
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<CategoryResponse> create(@Valid @RequestBody CategoryRequest r) {
    return ResponseEntity.status(HttpStatus.CREATED).body(categoryService.create(r));
  }

  @PatchMapping("/{id}")
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<CategoryResponse> update(
      @PathVariable UUID id, @Valid @RequestBody CategoryRequest r) {
    return ResponseEntity.ok(categoryService.update(id, r));
  }

  @DeleteMapping("/{id}")
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<Void> delete(@PathVariable UUID id) {
    categoryService.delete(id);
    return ResponseEntity.noContent().build();
  }
}
