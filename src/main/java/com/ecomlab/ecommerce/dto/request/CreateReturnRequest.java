package com.ecomlab.ecommerce.dto.request;

import jakarta.validation.constraints.*;
import jakarta.validation.Valid;
import java.util.UUID;
import java.util.List;
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

  @NotEmpty private List<@Valid ReturnItemRequest> items;

  public UUID orderId() {
    return orderId;
  }

  public String reason() {
    return reason;
  }
}
