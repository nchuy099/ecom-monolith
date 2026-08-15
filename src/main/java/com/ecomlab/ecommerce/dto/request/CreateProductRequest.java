package com.ecomlab.ecommerce.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.UUID;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateProductRequest {
  @NotNull private UUID categoryId;

  @NotBlank
  @Size(max = 160)
  private String name;

  @Size(max = 5000)
  private String description;
}
