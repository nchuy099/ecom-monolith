package com.ecomlab.ecommerce.dto.request;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateProductVariantRequest {
  @NotBlank
  @Size(max = 80)
  private String sku;

  @NotBlank
  @Size(max = 160)
  private String name;

  @NotNull
  @DecimalMin("0.00")
  private BigDecimal price;
}
