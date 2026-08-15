package com.ecomlab.ecommerce.dto.response;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CartResponse {
  private UUID id;
  private List<CartItemResponse> items;
  private BigDecimal totalAmount;
}
