package com.ecomlab.ecommerce.dto.response;

import java.util.UUID;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserResponse {
  private UUID id;
  private String email;
  private String displayName;
  private String role;
}
