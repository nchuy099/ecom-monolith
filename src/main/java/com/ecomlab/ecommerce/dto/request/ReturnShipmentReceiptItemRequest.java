package com.ecomlab.ecommerce.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import java.util.UUID;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ReturnShipmentReceiptItemRequest {
  @NotNull private UUID shipmentItemId;
  @Min(0) private int receivedQuantity;
  @Min(0) private int restockedQuantity;
}
