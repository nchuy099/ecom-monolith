package com.ecomlab.ecommerce.service.builder;

import com.ecomlab.ecommerce.dto.response.ProductAdminResponse;
import com.ecomlab.ecommerce.dto.response.ProductDetailProjection;
import com.ecomlab.ecommerce.dto.response.ProductListProjection;
import com.ecomlab.ecommerce.dto.response.ProductSummaryResponse;
import com.ecomlab.ecommerce.dto.response.ProductVariantAdminResponse;
import com.ecomlab.ecommerce.dto.response.ProductVariantDetailResponse;
import com.ecomlab.ecommerce.entity.ProductEntity;
import com.ecomlab.ecommerce.entity.ProductVariantEntity;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Map;

public final class ProductResponseBuilder {
  private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
  private static final TypeReference<Map<String, String>> ATTRIBUTES_TYPE =
      new TypeReference<>() {};

  private ProductResponseBuilder() {}

  public static ProductSummaryResponse summary(ProductEntity product) {
    return ProductSummaryResponse.builder()
        .id(product.getId())
        .name(product.getName())
        .categoryId(product.getCategory().getId())
        .categoryName(product.getCategory().getName())
        .rating(product.getRating())
        .reviewCount(product.getReviewCount())
        .badge(product.getBadge())
        .active(product.isActive())
        .build();
  }

  public static ProductSummaryResponse summary(ProductListProjection product) {
    return ProductSummaryResponse.builder()
        .id(product.id())
        .name(product.name())
        .categoryId(product.categoryId())
        .categoryName(product.categoryName())
        .active(product.active())
        .variantId(product.variantId())
        .sku(product.sku())
        .variantName(product.variantName())
        .price(product.price())
        .availableQuantity(product.availableQuantity())
        .reservedQuantity(product.reservedQuantity())
        .imageUrl(product.imageUrl())
        .rating(product.rating())
        .reviewCount(product.reviewCount())
        .badge(product.badge())
        .build();
  }

  public static ProductVariantDetailResponse detail(ProductVariantEntity variant) {
    return ProductVariantDetailResponse.builder()
        .productId(variant.getProduct().getId())
        .productName(variant.getProduct().getName())
        .variantId(variant.getId())
        .sku(variant.getSku())
        .variantName(variant.getName())
        .price(variant.getPrice())
        .build();
  }

  public static ProductVariantDetailResponse detail(ProductDetailProjection product) {
    return ProductVariantDetailResponse.builder()
        .productId(product.productId())
        .productName(product.productName())
        .variantId(product.variantId())
        .sku(product.sku())
        .variantName(product.variantName())
        .price(product.price())
        .availableQuantity(product.availableQuantity())
        .reservedQuantity(product.reservedQuantity())
        .imageUrl(product.imageUrl())
        .description(product.description())
        .attributes(attributes(product.attributesJson()))
        .build();
  }

  public static ProductAdminResponse admin(ProductEntity product) {
    return ProductAdminResponse.builder()
        .id(product.getId())
        .categoryId(product.getCategory().getId())
        .name(product.getName())
        .description(product.getDescription())
        .active(product.isActive())
        .build();
  }

  public static ProductVariantAdminResponse adminVariant(ProductVariantEntity variant) {
    return ProductVariantAdminResponse.builder()
        .id(variant.getId())
        .productId(variant.getProduct().getId())
        .sku(variant.getSku())
        .name(variant.getName())
        .price(variant.getPrice())
        .active(variant.isActive())
        .build();
  }

  private static Map<String, String> attributes(String attributesJson) {
    if (attributesJson == null || attributesJson.isBlank()) {
      return Map.of();
    }

    try {
      return OBJECT_MAPPER.readValue(attributesJson, ATTRIBUTES_TYPE);
    } catch (Exception exception) {
      return Map.of();
    }
  }
}
