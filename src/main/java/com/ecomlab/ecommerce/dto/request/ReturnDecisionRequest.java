package com.ecomlab.ecommerce.dto.request;

import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ReturnDecisionRequest {
  @Size(max = 1000) private String note;
}
