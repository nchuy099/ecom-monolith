package com.ecomlab.ecommerce.dto.response;

import java.util.UUID;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReturnResponse {
  private UUID id;
  private UUID orderId;
  private String status;
  private String reason;
}
