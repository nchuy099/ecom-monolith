package com.ecomlab.ecommerce.service.impl;

import com.ecomlab.ecommerce.common.enums.*;
import com.ecomlab.ecommerce.dto.request.CreatePaymentRequest;
import com.ecomlab.ecommerce.dto.request.PaymentWebhookRequest;
import com.ecomlab.ecommerce.dto.response.PaymentResponse;
import com.ecomlab.ecommerce.entity.*;
import com.ecomlab.ecommerce.exception.BusinessException;
import com.ecomlab.ecommerce.repository.*;
import com.ecomlab.ecommerce.service.NotificationService;
import com.ecomlab.ecommerce.service.PaymentService;
import com.ecomlab.ecommerce.service.builder.PaymentResponseBuilder;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {
  private final PaymentRepository paymentRepository;
  private final OrderRepository orderRepository;
  private final NotificationService notificationService;

  @Transactional
  public PaymentResponse create(UUID userId, UUID orderId, CreatePaymentRequest request) {
    OrderEntity order = order(orderId, userId);
    return PaymentResponseBuilder.build(
        createPending(order, request.getProvider(), request.getIdempotencyKey()));
  }

  @Transactional(readOnly = true)
  public List<PaymentResponse> getMyOrderPayments(UUID userId, UUID orderId) {
    order(orderId, userId);
    return paymentRepository.findActiveByOrderIdWithOrder(orderId).stream()
        .map(PaymentResponseBuilder::build)
        .toList();
  }

  @Transactional
  public PaymentResponse webhook(PaymentWebhookRequest request) {
    PaymentEntity payment =
        paymentRepository
            .findById(request.getPaymentId())
            .orElseThrow(
                () ->
                    new BusinessException(
                        "PAYMENT_NOT_FOUND", "PaymentEntity not found", HttpStatus.NOT_FOUND));
    PaymentStatus status;
    try {
      status = PaymentStatus.valueOf(request.getStatus().toUpperCase());
    } catch (IllegalArgumentException exception) {
      throw new BusinessException(
          "INVALID_PAYMENT_STATUS", "Invalid payment status", HttpStatus.BAD_REQUEST);
    }
    payment.setStatus(status);
    payment.setProviderReference(request.getProviderReference());
    payment.setPaidAt(status == PaymentStatus.SUCCEEDED ? Instant.now() : payment.getPaidAt());
    if (status == PaymentStatus.SUCCEEDED
        && payment.getOrder().getStatus() == OrderStatus.PENDING_PAYMENT) {
      payment.getOrder().setStatus(OrderStatus.CONFIRMED);
      notificationService.queuePaymentSucceeded(payment.getOrder());
    }
    return PaymentResponseBuilder.build(payment);
  }

  private OrderEntity order(UUID orderId, UUID userId) {
    return orderRepository
        .findOwnedByIdWithItems(orderId, userId)
        .orElseThrow(
            () ->
                new BusinessException("ORDER_NOT_FOUND", "Order not found", HttpStatus.NOT_FOUND));
  }

  @Override
  public PaymentEntity createPending(OrderEntity order, String provider, String idempotencyKey) {
    return paymentRepository
        .findByIdempotencyKey(idempotencyKey)
        .orElseGet(
            () -> {
              PaymentEntity payment = new PaymentEntity();
              payment.setOrder(order);
              payment.setProvider(provider);
              payment.setIdempotencyKey(idempotencyKey);
              payment.setAmount(order.getTotalAmount());
              payment.setStatus(PaymentStatus.PENDING);
              return paymentRepository.save(payment);
            });
  }
}
