package com.ecomlab.ecommerce.controller;

import com.ecomlab.ecommerce.dto.request.AddressRequest;
import com.ecomlab.ecommerce.dto.request.UpdateProfileRequest;
import com.ecomlab.ecommerce.dto.response.AddressResponse;
import com.ecomlab.ecommerce.dto.response.UserResponse;
import com.ecomlab.ecommerce.service.UserService;
import jakarta.validation.Valid;
import java.util.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/users/me")
@RequiredArgsConstructor
public class UserController {
  private final UserService userService;

  @GetMapping
  public ResponseEntity<UserResponse> me(Authentication a) {
    return ResponseEntity.ok(userService.me(UUID.fromString(a.getName())));
  }

  @PatchMapping
  public ResponseEntity<UserResponse> update(
      Authentication a, @Valid @RequestBody UpdateProfileRequest r) {
    return ResponseEntity.ok(userService.update(UUID.fromString(a.getName()), r.getDisplayName()));
  }

  @GetMapping("/addresses")
  public ResponseEntity<List<AddressResponse>> addresses(Authentication a) {
    return ResponseEntity.ok(userService.addresses(UUID.fromString(a.getName())));
  }

  @PostMapping("/addresses")
  public ResponseEntity<AddressResponse> createAddress(
      Authentication a, @Valid @RequestBody AddressRequest r) {
    return ResponseEntity.status(HttpStatus.CREATED)
        .body(userService.createAddress(UUID.fromString(a.getName()), r));
  }

  @PatchMapping("/addresses/{id}")
  public ResponseEntity<AddressResponse> updateAddress(
      Authentication a, @PathVariable UUID id, @Valid @RequestBody AddressRequest r) {
    return ResponseEntity.ok(userService.updateAddress(UUID.fromString(a.getName()), id, r));
  }

  @DeleteMapping("/addresses/{id}")
  public ResponseEntity<Void> deleteAddress(Authentication a, @PathVariable UUID id) {
    userService.deleteAddress(UUID.fromString(a.getName()), id);
    return ResponseEntity.noContent().build();
  }

  @PatchMapping("/addresses/{id}/default")
  public ResponseEntity<Void> defaultAddress(Authentication a, @PathVariable UUID id) {
    userService.setDefaultAddress(UUID.fromString(a.getName()), id);
    return ResponseEntity.noContent().build();
  }
}
