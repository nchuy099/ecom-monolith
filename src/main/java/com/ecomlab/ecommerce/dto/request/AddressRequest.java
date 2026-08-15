package com.ecomlab.ecommerce.dto.request;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AddressRequest {
  @NotBlank private String recipientName;
  @NotBlank private String phone;
  @NotBlank private String addressLine;
  @NotBlank private String city;

  @NotNull
  @DecimalMin("-90")
  @DecimalMax("90")
  private BigDecimal latitude;

  @NotNull
  @DecimalMin("-180")
  @DecimalMax("180")
  private BigDecimal longitude;

  private boolean defaultAddress;

  public String recipientName() {
    return recipientName;
  }

  public String phone() {
    return phone;
  }

  public String addressLine() {
    return addressLine;
  }

  public String city() {
    return city;
  }

  public BigDecimal latitude() {
    return latitude;
  }

  public BigDecimal longitude() {
    return longitude;
  }

  public boolean defaultAddress() {
    return defaultAddress;
  }
}
