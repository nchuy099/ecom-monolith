package com.ecomlab.ecommerce.service.builder;

import com.ecomlab.ecommerce.dto.response.TokenPairResponse;

public final class TokenPairResponseBuilder {
  private TokenPairResponseBuilder() {}

  public static TokenPairResponse build(String accessToken, String refreshToken) {
    return TokenPairResponse.builder().accessToken(accessToken).refreshToken(refreshToken).build();
  }
}
