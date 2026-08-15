package com.ecomlab.ecommerce.service.impl;

import com.ecomlab.ecommerce.dto.request.CartItemRequest;
import com.ecomlab.ecommerce.dto.response.CartResponse;
import com.ecomlab.ecommerce.entity.CartEntity;
import com.ecomlab.ecommerce.entity.CartItemEntity;
import com.ecomlab.ecommerce.entity.ProductVariantEntity;
import com.ecomlab.ecommerce.entity.UserEntity;
import com.ecomlab.ecommerce.exception.BusinessException;
import com.ecomlab.ecommerce.repository.CartRepository;
import com.ecomlab.ecommerce.repository.ProductVariantRepository;
import com.ecomlab.ecommerce.repository.UserRepository;
import com.ecomlab.ecommerce.service.CartService;
import com.ecomlab.ecommerce.service.builder.CartResponseBuilder;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CartServiceImpl implements CartService {
  private final UserRepository userRepository;
  private final CartRepository cartRepository;
  private final ProductVariantRepository productVariantRepository;

  @Override
  @Transactional(readOnly = true)
  public CartResponse cart(UUID userId) {
    return CartResponseBuilder.build(currentCart(userId));
  }

  @Override
  @Transactional
  public CartResponse addCartItem(UUID userId, CartItemRequest request) {
    CartEntity cart = currentOrCreate(userId);
    ProductVariantEntity variant = activeVariant(request.variantId());

    for (CartItemEntity item : cart.getItems()) {
      if (item.getVariant().getId().equals(request.variantId())) {
        item.setQuantity(item.getQuantity() + request.quantity());
        return CartResponseBuilder.build(cart);
      }
    }

    CartItemEntity item = new CartItemEntity();
    item.setCart(cart);
    item.setVariant(variant);
    item.setQuantity(request.quantity());
    cart.getItems().add(item);

    return CartResponseBuilder.build(cart);
  }

  @Override
  @Transactional
  public CartResponse updateCartItem(UUID userId, UUID itemId, int quantity) {
    CartEntity cart = currentCart(userId);
    CartItemEntity item =
        cart.getItems().stream()
            .filter(value -> value.getId().equals(itemId))
            .findFirst()
            .orElseThrow(
                () ->
                    new BusinessException(
                        "CART_ITEM_NOT_FOUND", "Cart item not found", HttpStatus.NOT_FOUND));

    item.setQuantity(quantity);
    return CartResponseBuilder.build(cart);
  }

  @Override
  @Transactional
  public void removeCartItem(UUID userId, UUID itemId) {
    currentCart(userId).getItems().removeIf(item -> item.getId().equals(itemId));
  }

  @Override
  @Transactional
  public void clearCart(UUID userId) {
    currentCart(userId).getItems().clear();
  }

  private UserEntity user(UUID id) {
    return userRepository
        .findById(id)
        .filter(value -> !value.isDeleted())
        .orElseThrow(
            () -> new BusinessException("USER_NOT_FOUND", "User not found", HttpStatus.NOT_FOUND));
  }

  private CartEntity currentCart(UUID userId) {
    return cartRepository
        .findCurrentByUserId(userId)
        .orElseThrow(
            () -> new BusinessException("CART_NOT_FOUND", "Cart not found", HttpStatus.NOT_FOUND));
  }

  private CartEntity currentOrCreate(UUID userId) {
    return cartRepository
        .findCurrentByUserId(userId)
        .orElseGet(
            () -> {
              CartEntity cart = new CartEntity();
              cart.setUser(user(userId));
              return cartRepository.save(cart);
            });
  }

  private ProductVariantEntity activeVariant(UUID variantId) {
    return productVariantRepository
        .findByIdAndIsDeletedFalseAndActiveTrue(variantId)
        .orElseThrow(
            () ->
                new BusinessException(
                    "PRODUCT_VARIANT_NOT_FOUND",
                    "Product variant not found",
                    HttpStatus.NOT_FOUND));
  }
}
