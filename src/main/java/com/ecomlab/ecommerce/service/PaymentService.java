package com.ecomlab.ecommerce.service;

import com.ecomlab.ecommerce.dto.request.CreatePaymentRequest;
import com.ecomlab.ecommerce.dto.request.PaymentWebhookRequest;
import com.ecomlab.ecommerce.dto.response.PaymentResponse;
import com.ecomlab.ecommerce.entity.OrderEntity;
import com.ecomlab.ecommerce.entity.PaymentEntity;
import java.util.List;
import java.util.UUID;

public interface PaymentService {
  PaymentResponse create(UUID userId, UUID orderId, CreatePaymentRequest request);

  List<PaymentResponse> getMyOrderPayments(UUID userId, UUID orderId);

  PaymentResponse webhook(PaymentWebhookRequest request);

  PaymentEntity createPending(OrderEntity order, String provider, String idempotencyKey);
}
