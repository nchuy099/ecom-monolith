package com.ecomlab.ecommerce.service.builder;

import com.ecomlab.ecommerce.dto.response.NotificationResponse;
import com.ecomlab.ecommerce.entity.NotificationEntity;

public final class NotificationResponseBuilder {
  private NotificationResponseBuilder() {}

  public static NotificationResponse build(NotificationEntity notification) {
    return NotificationResponse.builder()
        .id(notification.getId())
        .userId(notification.getUser() == null ? null : notification.getUser().getId().toString())
        .title(notification.getSubject())
        .message(notification.getBody())
        .subject(notification.getSubject())
        .body(notification.getBody())
        .status(notification.getStatus().name())
        .attemptCount(notification.getAttemptCount())
        .read(notification.getReadAt() != null)
        .createdAt(notification.getCreatedAt())
        .build();
  }
}
