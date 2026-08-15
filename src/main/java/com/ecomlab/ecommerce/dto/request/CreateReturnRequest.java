package com.ecomlab.ecommerce.dto.request;

import jakarta.validation.constraints.*;
import java.util.UUID;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateReturnRequest {
  @NotNull private UUID orderId;

  @NotBlank
  @Size(max = 1000)
  private String reason;

  public UUID orderId() {
    return orderId;
  }

  public String reason() {
    return reason;
  }
}
