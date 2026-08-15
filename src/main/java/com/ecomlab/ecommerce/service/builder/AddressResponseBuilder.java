package com.ecomlab.ecommerce.service.builder;

import com.ecomlab.ecommerce.dto.response.AddressResponse;
import com.ecomlab.ecommerce.entity.UserAddressEntity;

public final class AddressResponseBuilder {
  private AddressResponseBuilder() {}

  public static AddressResponse build(UserAddressEntity address) {
    return AddressResponse.builder()
        .id(address.getId())
        .recipientName(address.getRecipientName())
        .phone(address.getPhone())
        .addressLine(address.getAddressLine())
        .city(address.getCity())
        .latitude(address.getLatitude())
        .longitude(address.getLongitude())
        .defaultAddress(address.isDefaultAddress())
        .build();
  }
}
