package com.ecomlab.ecommerce.service.builder;

import com.ecomlab.ecommerce.dto.response.CartItemResponse;
import com.ecomlab.ecommerce.dto.response.CartResponse;
import com.ecomlab.ecommerce.entity.CartEntity;
import java.math.BigDecimal;
import java.util.List;

public final class CartResponseBuilder {
  private CartResponseBuilder() {}

  public static CartResponse build(CartEntity cart) {
    List<CartItemResponse> items =
        cart.getItems().stream()
            .map(
                item -> {
                  BigDecimal subtotal =
                      item.getVariant().getPrice().multiply(BigDecimal.valueOf(item.getQuantity()));
                  return CartItemResponse.builder()
                      .id(item.getId())
                      .variantId(item.getVariant().getId())
                      .productId(item.getVariant().getProduct().getId())
                      .productName(item.getVariant().getProduct().getName())
                      .variantName(item.getVariant().getName())
                      .sku(item.getVariant().getSku())
                      .name(item.getVariant().getName())
                      .price(item.getVariant().getPrice())
                      .quantity(item.getQuantity())
                      .lineTotal(subtotal)
                      .subtotal(subtotal)
                      .imageUrl(item.getVariant().getImageUrl())
                      .availableQuantity(0)
                      .build();
                })
            .toList();
    return CartResponse.builder()
        .id(cart.getId())
        .items(items)
        .totalAmount(
            items.stream()
                .map(CartItemResponse::getSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add))
        .itemCount(items.stream().mapToInt(CartItemResponse::getQuantity).sum())
        .build();
  }
}
