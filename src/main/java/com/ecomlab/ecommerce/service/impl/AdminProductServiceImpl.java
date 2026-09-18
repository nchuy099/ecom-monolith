package com.ecomlab.ecommerce.service.impl;

import com.ecomlab.ecommerce.common.enums.*;
import com.ecomlab.ecommerce.dto.request.CreateProductRequest;
import com.ecomlab.ecommerce.dto.request.CreateProductVariantRequest;
import com.ecomlab.ecommerce.dto.response.CursorPageResponse;
import com.ecomlab.ecommerce.dto.response.ProductAdminResponse;
import com.ecomlab.ecommerce.dto.response.ProductSummaryResponse;
import com.ecomlab.ecommerce.dto.response.ProductVariantAdminResponse;
import com.ecomlab.ecommerce.entity.*;
import com.ecomlab.ecommerce.exception.BusinessException;
import com.ecomlab.ecommerce.repository.*;
import com.ecomlab.ecommerce.service.AdminProductService;
import com.ecomlab.ecommerce.service.ProductCatalogService;
import com.ecomlab.ecommerce.service.builder.ProductResponseBuilder;
import java.math.BigDecimal;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AdminProductServiceImpl implements AdminProductService {
  private final ProductRepository productRepository;
  private final CategoryRepository categoryRepository;
  private final ProductVariantRepository productVariantRepository;
  private final ProductCatalogService productCatalogService;

  @Transactional(readOnly = true)
  public CursorPageResponse<ProductSummaryResponse> list(
      String keyword,
      UUID categoryId,
      BigDecimal minPrice,
      BigDecimal maxPrice,
      boolean inStockOnly,
      String sort,
      String cursor,
      int size) {
    return productCatalogService.search(
        keyword, categoryId, minPrice, maxPrice, inStockOnly, sort, cursor, size);
  }

  @Transactional
  public ProductAdminResponse create(CreateProductRequest request) {
    CategoryEntity category = category(request.getCategoryId());
    ProductEntity product = new ProductEntity();
    product.setCategory(category);
    product.setName(request.getName());
    product.setDescription(request.getDescription());
    product.setActive(true);
    productRepository.save(product);
    return ProductResponseBuilder.admin(product);
  }

  @Transactional
  public ProductAdminResponse update(UUID id, CreateProductRequest request) {
    ProductEntity product = product(id);
    CategoryEntity category = category(request.getCategoryId());
    product.setCategory(category);
    product.setName(request.getName());
    product.setDescription(request.getDescription());
    productCatalogService.evictDetail(product.getId());
    return ProductResponseBuilder.admin(product);
  }

  @Transactional
  public void delete(UUID id) {
    ProductEntity product = product(id);
    product.softDelete();
    productCatalogService.evictDetail(product.getId());
  }

  @Transactional
  public ProductVariantAdminResponse createVariant(
      UUID productId, CreateProductVariantRequest request) {
    ProductEntity product = product(productId);
    ProductVariantEntity variant = new ProductVariantEntity();
    variant.setProduct(product);
    variant.setSku(request.getSku());
    variant.setName(request.getName());
    variant.setPrice(request.getPrice());
    variant.setActive(true);
    ProductVariantEntity saved = productVariantRepository.save(variant);
    productCatalogService.evictDetail(product.getId());
    return ProductResponseBuilder.adminVariant(saved);
  }

  @Transactional
  public ProductVariantAdminResponse updateVariant(
      UUID productId, UUID variantId, CreateProductVariantRequest request) {
    ProductVariantEntity variant = variant(productId, variantId);
    variant.setSku(request.getSku());
    variant.setName(request.getName());
    variant.setPrice(request.getPrice());
    productCatalogService.evictDetail(variant.getProduct().getId());
    return ProductResponseBuilder.adminVariant(variant);
  }

  @Transactional
  public void deleteVariant(UUID productId, UUID variantId) {
    ProductVariantEntity variant = variant(productId, variantId);
    variant.softDelete();
    productCatalogService.evictDetail(variant.getProduct().getId());
  }

  private ProductEntity product(UUID id) {
    return productRepository
        .findById(id)
        .filter(product -> !product.isDeleted())
        .orElseThrow(
            () ->
                new BusinessException(
                    "PRODUCT_NOT_FOUND", "ProductEntity not found", HttpStatus.NOT_FOUND));
  }

  private ProductVariantEntity variant(UUID productId, UUID variantId) {
    return productVariantRepository
        .findActiveByProductIdAndId(productId, variantId)
        .orElseThrow(
            () ->
                new BusinessException(
                    "PRODUCT_VARIANT_NOT_FOUND",
                    "ProductEntity variant not found",
                    HttpStatus.NOT_FOUND));
  }

  private CategoryEntity category(UUID id) {
    return categoryRepository
        .findByIdAndIsDeletedFalse(id)
        .orElseThrow(
            () ->
                new BusinessException(
                    "CATEGORY_NOT_FOUND", "CategoryEntity not found", HttpStatus.NOT_FOUND));
  }
}
