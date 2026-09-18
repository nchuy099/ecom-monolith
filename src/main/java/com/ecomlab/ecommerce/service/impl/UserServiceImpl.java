package com.ecomlab.ecommerce.service.impl;

import com.ecomlab.ecommerce.common.enums.Role;
import com.ecomlab.ecommerce.dto.request.AddressRequest;
import com.ecomlab.ecommerce.dto.response.AddressResponse;
import com.ecomlab.ecommerce.dto.response.UserResponse;
import com.ecomlab.ecommerce.entity.UserAddressEntity;
import com.ecomlab.ecommerce.entity.UserEntity;
import com.ecomlab.ecommerce.exception.BusinessException;
import com.ecomlab.ecommerce.repository.AddressRepository;
import com.ecomlab.ecommerce.repository.UserRepository;
import com.ecomlab.ecommerce.service.UserService;
import com.ecomlab.ecommerce.service.builder.AddressResponseBuilder;
import com.ecomlab.ecommerce.service.builder.UserResponseBuilder;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
  private final UserRepository userRepository;
  private final AddressRepository addressRepository;

  @Override
  @Transactional(readOnly = true)
  public UserResponse me(UUID userId) {
    return UserResponseBuilder.build(user(userId));
  }

  @Override
  @Transactional
  public UserResponse update(UUID userId, String displayName) {
    UserEntity user = user(userId);
    user.setDisplayName(displayName);
    return UserResponseBuilder.build(user);
  }

  @Override
  @Transactional(readOnly = true)
  public List<AddressResponse> addresses(UUID userId) {
    return addressRepository.findAllActiveByUserId(userId).stream()
        .map(AddressResponseBuilder::build)
        .toList();
  }

  @Override
  @Transactional
  public AddressResponse createAddress(UUID userId, AddressRequest request) {
    UserAddressEntity address = new UserAddressEntity();
    address.setUser(user(userId));
    apply(address, request);

    if (request.defaultAddress()) {
      clearDefault(userId);
    }

    return AddressResponseBuilder.build(addressRepository.save(address));
  }

  @Override
  @Transactional
  public AddressResponse updateAddress(UUID userId, UUID addressId, AddressRequest request) {
    UserAddressEntity address = ownedAddress(userId, addressId);
    apply(address, request);

    if (request.defaultAddress()) {
      clearDefaultExcept(userId, addressId);
    }

    return AddressResponseBuilder.build(address);
  }

  @Override
  @Transactional
  public void deleteAddress(UUID userId, UUID addressId) {
    ownedAddress(userId, addressId).softDelete();
  }

  @Override
  @Transactional
  public void setDefaultAddress(UUID userId, UUID addressId) {
    clearDefaultExcept(userId, addressId);
    ownedAddress(userId, addressId).setDefaultAddress(true);
  }

  @Override
  @Transactional(readOnly = true)
  public List<UserResponse> shippers() {
    return userRepository.findActiveByRole(Role.SHIPPER).stream()
        .map(UserResponseBuilder::build)
        .toList();
  }

  private UserEntity user(UUID id) {
    return userRepository
        .findById(id)
        .filter(user -> !user.isDeleted())
        .orElseThrow(
            () -> new BusinessException("USER_NOT_FOUND", "User not found", HttpStatus.NOT_FOUND));
  }

  private UserAddressEntity ownedAddress(UUID userId, UUID addressId) {
    return addressRepository
        .findOwnedById(addressId, userId)
        .orElseThrow(
            () ->
                new BusinessException(
                    "USER_ADDRESS_NOT_FOUND", "Address not found", HttpStatus.NOT_FOUND));
  }

  private void clearDefault(UUID userId) {
    addressRepository.clearDefaultAddress(userId, null);
  }

  private void clearDefaultExcept(UUID userId, UUID addressId) {
    addressRepository.clearDefaultAddress(userId, addressId);
  }

  private void apply(UserAddressEntity address, AddressRequest request) {
    address.setRecipientName(request.recipientName());
    address.setPhone(request.phone());
    address.setAddressLine(request.addressLine());
    address.setCity(request.city());
    address.setLatitude(request.latitude());
    address.setLongitude(request.longitude());
    address.setDefaultAddress(request.defaultAddress());
  }
}
