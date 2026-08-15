package com.ecomlab.ecommerce.dto.response;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentResponse {
  private UUID id;
  private UUID orderId;
  private String provider;
  private String providerReference;
  private String idempotencyKey;
  private BigDecimal amount;
  private String status;
  private Instant paidAt;
}
