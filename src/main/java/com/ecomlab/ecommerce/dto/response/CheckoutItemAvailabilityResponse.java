package com.ecomlab.ecommerce.dto.response;

import java.util.UUID;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CheckoutItemAvailabilityResponse {
  private UUID variantId;
  private String sku;
  private String productName;
  private String variantName;
  private int requestedQuantity;
  private int availableInSelectedZone;
  private int missingQuantity;
  private boolean available;
}
