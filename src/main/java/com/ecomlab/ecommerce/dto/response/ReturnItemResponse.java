package com.ecomlab.ecommerce.dto.response;

import java.math.BigDecimal;
import java.util.UUID;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReturnItemResponse {
  private UUID id;
  private UUID orderItemId;
  private String sku;
  private String name;
  private BigDecimal unitPrice;
  private int requestedQuantity;
  private int receivedQuantity;
  private int restockedQuantity;
}
