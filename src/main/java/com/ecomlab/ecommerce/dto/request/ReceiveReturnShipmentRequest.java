package com.ecomlab.ecommerce.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import java.util.List;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ReceiveReturnShipmentRequest {
  @NotEmpty private List<@Valid ReturnShipmentReceiptItemRequest> items;
}
