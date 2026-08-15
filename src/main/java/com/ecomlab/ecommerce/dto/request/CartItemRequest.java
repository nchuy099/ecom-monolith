package com.ecomlab.ecommerce.dto.request;

import jakarta.validation.constraints.*;
import java.util.UUID;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CartItemRequest {
  @NotNull private UUID variantId;

  @Min(1)
  private int quantity;

  public UUID variantId() {
    return variantId;
  }

  public int quantity() {
    return quantity;
  }
}
