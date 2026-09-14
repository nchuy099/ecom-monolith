package com.ecomlab.ecommerce.dto.response;

import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WarehouseShippingZoneResponse {
  private UUID id;
  private UUID shippingZoneId;
  private String shippingZoneCode;
  private String shippingZoneName;
  private int priority;
  private boolean active;
}
