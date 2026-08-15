package com.ecomlab.ecommerce.service;

import com.ecomlab.ecommerce.dto.response.NotificationResponse;
import com.ecomlab.ecommerce.dto.response.PageResponse;
import com.ecomlab.ecommerce.entity.OrderEntity;
import java.util.List;
import java.util.UUID;

public interface NotificationService {
  List<NotificationResponse> getMyNotifications(UUID userId);

  NotificationResponse read(UUID userId, UUID notificationId);

  PageResponse<NotificationResponse> adminPage(String status, int page, int size);

  void queueOrderCreated(OrderEntity order);

  void queuePaymentSucceeded(OrderEntity order);

  void queueShipmentDelivered(OrderEntity order);
}
