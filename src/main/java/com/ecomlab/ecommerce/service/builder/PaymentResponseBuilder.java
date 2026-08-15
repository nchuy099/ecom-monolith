package com.ecomlab.ecommerce.service.builder;

import com.ecomlab.ecommerce.dto.response.PaymentResponse;
import com.ecomlab.ecommerce.entity.PaymentEntity;

public final class PaymentResponseBuilder {
  private PaymentResponseBuilder() {}

  public static PaymentResponse build(PaymentEntity payment) {
    return PaymentResponse.builder()
        .id(payment.getId())
        .orderId(payment.getOrder().getId())
        .provider(payment.getProvider())
        .providerReference(payment.getProviderReference())
        .idempotencyKey(payment.getIdempotencyKey())
        .amount(payment.getAmount())
        .status(payment.getStatus().name())
        .paidAt(payment.getPaidAt())
        .build();
  }
}
