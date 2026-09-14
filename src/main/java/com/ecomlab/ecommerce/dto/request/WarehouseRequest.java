package com.ecomlab.ecommerce.dto.request;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WarehouseRequest {
  @NotBlank private String code;
  @NotBlank private String name;
  @NotBlank private String addressLine;
  @NotBlank private String priorityArea;
  @NotNull private BigDecimal latitude;
  @NotNull private BigDecimal longitude;

  public String code() {
    return code;
  }

  public String name() {
    return name;
  }

  public String addressLine() {
    return addressLine;
  }

  public String priorityArea() {
    return priorityArea;
  }

  public BigDecimal latitude() {
    return latitude;
  }

  public BigDecimal longitude() {
    return longitude;
  }
}
