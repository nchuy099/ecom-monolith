package com.ecomlab.ecommerce.dto.response;

import java.math.BigDecimal;
import java.util.UUID;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AddressResponse {
  private UUID id;
  private String recipientName;
  private String phone;
  private String addressLine;
  private String city;
  private BigDecimal latitude;
  private BigDecimal longitude;
  private boolean defaultAddress;
}
