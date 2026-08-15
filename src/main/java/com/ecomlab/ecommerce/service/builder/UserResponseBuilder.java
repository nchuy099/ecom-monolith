package com.ecomlab.ecommerce.service.builder;

import com.ecomlab.ecommerce.dto.response.UserResponse;
import com.ecomlab.ecommerce.entity.UserEntity;

public final class UserResponseBuilder {
  private UserResponseBuilder() {}

  public static UserResponse build(UserEntity user) {
    return UserResponse.builder()
        .id(user.getId())
        .email(user.getEmail())
        .displayName(user.getDisplayName())
        .role(user.getRole().name())
        .build();
  }
}
