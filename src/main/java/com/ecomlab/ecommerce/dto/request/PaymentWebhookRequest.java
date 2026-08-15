package com.ecomlab.ecommerce.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.UUID;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentWebhookRequest {
  @NotNull private UUID paymentId;
  @NotBlank private String status;
  private String providerReference;
}
