package com.ecomlab.ecommerce.controller;

import com.ecomlab.ecommerce.dto.request.CartItemRequest;
import com.ecomlab.ecommerce.dto.response.CartResponse;
import com.ecomlab.ecommerce.service.CartService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NonNull;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/cart")
@RequiredArgsConstructor
public class CartController {
  private final CartService cartService;

  @GetMapping
  public ResponseEntity<CartResponse> get(@NonNull Authentication a) {
    return ResponseEntity.ok(cartService.cart(UUID.fromString(a.getName())));
  }

  @PostMapping("/items")
  public ResponseEntity<CartResponse> add(Authentication a, @Valid @RequestBody CartItemRequest r) {
    return ResponseEntity.status(HttpStatus.CREATED)
        .body(cartService.addCartItem(UUID.fromString(a.getName()), r));
  }

  @PatchMapping("/items/{id}")
  public ResponseEntity<CartResponse> update(
      Authentication a, @PathVariable UUID id, @RequestParam @Min(1) int quantity) {
    return ResponseEntity.ok(
        cartService.updateCartItem(UUID.fromString(a.getName()), id, quantity));
  }

  @DeleteMapping("/items/{id}")
  public ResponseEntity<Void> remove(Authentication a, @PathVariable UUID id) {
    cartService.removeCartItem(UUID.fromString(a.getName()), id);
    return ResponseEntity.noContent().build();
  }

  @DeleteMapping
  public ResponseEntity<Void> clear(Authentication a) {
    cartService.clearCart(UUID.fromString(a.getName()));
    return ResponseEntity.noContent().build();
  }
}
