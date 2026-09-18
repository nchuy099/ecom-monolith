package com.ecomlab.ecommerce.dto.response;

import com.fasterxml.jackson.annotation.JsonIgnore;
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
  private String userId;
  private String title;
  private String message;
  @JsonIgnore private String subject;
  @JsonIgnore private String body;
  private String status;
  private int attemptCount;
  private boolean read;
  private Instant createdAt;

  public String getSubject() {
    return title;
  }

  public String getBody() {
    return message;
  }
}
