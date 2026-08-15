package com.ecomlab.ecommerce.service;

import com.ecomlab.ecommerce.dto.response.TokenPairResponse;

public interface AuthenticationService {
  TokenPairResponse register(String email, String password, String displayName, String deviceName);

  TokenPairResponse login(String email, String password, String deviceName);

  TokenPairResponse refresh(String refreshToken, String deviceName);

  void logout(String refreshToken);
}
