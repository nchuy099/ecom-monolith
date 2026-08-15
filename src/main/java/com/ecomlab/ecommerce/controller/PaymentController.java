package com.ecomlab.ecommerce.controller;

import com.ecomlab.ecommerce.dto.request.CreatePaymentRequest;
import com.ecomlab.ecommerce.dto.request.PaymentWebhookRequest;
import com.ecomlab.ecommerce.dto.response.PaymentResponse;
import com.ecomlab.ecommerce.service.PaymentService;
import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class PaymentController {
  private final PaymentService paymentService;

  @PostMapping("/orders/{orderId}/payments")
  public ResponseEntity<PaymentResponse> create(
      Authentication authentication,
      @PathVariable UUID orderId,
      @Valid @RequestBody CreatePaymentRequest request) {
    return ResponseEntity.status(HttpStatus.CREATED)
        .body(paymentService.create(UUID.fromString(authentication.getName()), orderId, request));
  }

  @GetMapping("/orders/{orderId}/payments")
  public ResponseEntity<List<PaymentResponse>> list(
      Authentication authentication, @PathVariable UUID orderId) {
    return ResponseEntity.ok(
        paymentService.getMyOrderPayments(UUID.fromString(authentication.getName()), orderId));
  }

  @PostMapping("/payments/webhook")
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<PaymentResponse> webhook(
      @Valid @RequestBody PaymentWebhookRequest request) {
    return ResponseEntity.ok(paymentService.webhook(request));
  }
}
