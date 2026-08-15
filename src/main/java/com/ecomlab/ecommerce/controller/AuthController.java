package com.ecomlab.ecommerce.controller;

import com.ecomlab.ecommerce.dto.request.LoginRequest;
import com.ecomlab.ecommerce.dto.request.RefreshTokenRequest;
import com.ecomlab.ecommerce.dto.request.RegisterRequest;
import com.ecomlab.ecommerce.dto.response.TokenPairResponse;
import com.ecomlab.ecommerce.service.AuthenticationService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {
  private final AuthenticationService authenticationService;

  @PostMapping("/register")
  public ResponseEntity<TokenPairResponse> register(@Valid @RequestBody RegisterRequest request) {
    TokenPairResponse response =
        authenticationService.register(
            request.getEmail(),
            request.getPassword(),
            request.getDisplayName(),
            request.getDeviceName());
    return ResponseEntity.status(HttpStatus.CREATED).body(response);
  }

  @PostMapping("/login")
  public ResponseEntity<TokenPairResponse> login(@Valid @RequestBody LoginRequest request) {
    return ResponseEntity.ok(
        authenticationService.login(
            request.getEmail(), request.getPassword(), request.getDeviceName()));
  }

  @PostMapping("/refresh")
  public ResponseEntity<TokenPairResponse> refresh(
      @Valid @RequestBody RefreshTokenRequest request) {
    return ResponseEntity.ok(
        authenticationService.refresh(request.getRefreshToken(), request.getDeviceName()));
  }

  @PostMapping("/logout")
  public ResponseEntity<Void> logout(@Valid @RequestBody RefreshTokenRequest request) {
    authenticationService.logout(request.getRefreshToken());
    return ResponseEntity.noContent().build();
  }
}
