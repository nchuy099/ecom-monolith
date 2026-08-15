package com.ecomlab.ecommerce.dto.request;

import jakarta.validation.constraints.*;
import java.util.UUID;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CategoryRequest {
  private UUID parentId;

  @NotBlank
  @Size(max = 160)
  private String name;

  @Size(max = 1000)
  private String description;

  public UUID parentId() {
    return parentId;
  }

  public String name() {
    return name;
  }

  public String description() {
    return description;
  }
}
