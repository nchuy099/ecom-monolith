package com.ecomlab.ecommerce.dto.response;

import java.math.BigDecimal;
import java.util.UUID;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WarehouseResponse {
  private UUID id;
  private String code;
  private String name;
  private String addressLine;
  private BigDecimal latitude;
  private BigDecimal longitude;
  private boolean active;
}
