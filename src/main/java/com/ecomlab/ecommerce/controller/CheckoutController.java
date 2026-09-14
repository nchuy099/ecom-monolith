package com.ecomlab.ecommerce.controller;

import com.ecomlab.ecommerce.dto.request.CheckoutRequest;
import com.ecomlab.ecommerce.dto.response.CheckoutResponse;
import com.ecomlab.ecommerce.service.CheckoutService;
import jakarta.validation.Valid;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/checkout")
@RequiredArgsConstructor
public class CheckoutController {
  private final CheckoutService checkoutService;

  @PostMapping
  @PreAuthorize("hasRole('CUSTOMER')")
  public ResponseEntity<CheckoutResponse> checkout(
      Authentication authentication, @Valid @RequestBody CheckoutRequest request) {
    return ResponseEntity.status(HttpStatus.CREATED)
        .body(
            checkoutService.checkout(
                UUID.fromString(authentication.getName()), request.getAddressId()));
  }

  @PostMapping("/preview")
  @PreAuthorize("hasRole('CUSTOMER')")
  public ResponseEntity<CheckoutResponse> preview(
      Authentication authentication, @Valid @RequestBody CheckoutRequest request) {
    return ResponseEntity.ok(
        checkoutService.preview(UUID.fromString(authentication.getName()), request.getAddressId()));
  }
}
