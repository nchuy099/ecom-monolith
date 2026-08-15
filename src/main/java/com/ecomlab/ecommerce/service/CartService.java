package com.ecomlab.ecommerce.service;

import com.ecomlab.ecommerce.dto.request.CartItemRequest;
import com.ecomlab.ecommerce.dto.response.CartResponse;
import java.util.UUID;

public interface CartService {
  CartResponse cart(UUID userId);

  CartResponse addCartItem(UUID userId, CartItemRequest request);

  CartResponse updateCartItem(UUID userId, UUID itemId, int quantity);

  void removeCartItem(UUID userId, UUID itemId);

  void clearCart(UUID userId);
}
