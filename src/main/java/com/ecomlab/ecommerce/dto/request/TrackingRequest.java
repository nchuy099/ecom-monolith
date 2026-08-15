package com.ecomlab.ecommerce.dto.request;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TrackingRequest {
  @NotBlank private String status;

  @Size(max = 1000)
  private String note;

  @DecimalMin("-90.0")
  @DecimalMax("90.0")
  private BigDecimal latitude;

  @DecimalMin("-180.0")
  @DecimalMax("180.0")
  private BigDecimal longitude;

  @Size(max = 500)
  private String proofUrl;
}
