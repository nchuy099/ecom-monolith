package com.ecomlab.ecommerce.dto.response;

import java.time.Instant;
import java.util.UUID;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotificationResponse {
  private UUID id;
  private String subject;
  private String body;
  private String status;
  private int attemptCount;
  private Instant createdAt;
}
