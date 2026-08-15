package com.ecomlab.ecommerce.service;

import com.ecomlab.ecommerce.dto.request.AddressRequest;
import com.ecomlab.ecommerce.dto.response.AddressResponse;
import com.ecomlab.ecommerce.dto.response.UserResponse;
import java.util.List;
import java.util.UUID;

public interface UserService {
  UserResponse me(UUID userId);

  UserResponse update(UUID userId, String displayName);

  List<AddressResponse> addresses(UUID userId);

  AddressResponse createAddress(UUID userId, AddressRequest request);

  AddressResponse updateAddress(UUID userId, UUID addressId, AddressRequest request);

  void deleteAddress(UUID userId, UUID addressId);

  void setDefaultAddress(UUID userId, UUID addressId);
}
