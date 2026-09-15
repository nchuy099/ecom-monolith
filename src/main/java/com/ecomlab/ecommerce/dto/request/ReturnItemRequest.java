package com.ecomlab.ecommerce.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import java.util.UUID;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ReturnItemRequest {
  @NotNull private UUID orderItemId;
  @Min(1) private int quantity;
}
