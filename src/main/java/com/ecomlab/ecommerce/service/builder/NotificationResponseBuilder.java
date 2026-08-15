package com.ecomlab.ecommerce.service.builder;

import com.ecomlab.ecommerce.dto.response.NotificationResponse;
import com.ecomlab.ecommerce.entity.NotificationEntity;

public final class NotificationResponseBuilder {
  private NotificationResponseBuilder() {}

  public static NotificationResponse build(NotificationEntity notification) {
    return NotificationResponse.builder()
        .id(notification.getId())
        .subject(notification.getSubject())
        .body(notification.getBody())
        .status(notification.getStatus().name())
        .attemptCount(notification.getAttemptCount())
        .createdAt(notification.getCreatedAt())
        .build();
  }
}
