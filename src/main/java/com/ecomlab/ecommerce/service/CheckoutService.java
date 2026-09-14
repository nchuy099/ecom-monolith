package com.ecomlab.ecommerce.service;

import com.ecomlab.ecommerce.dto.response.CheckoutResponse;
import java.util.UUID;

public interface CheckoutService {
  CheckoutResponse checkout(UUID userId, UUID addressId);

  CheckoutResponse preview(UUID userId, UUID addressId);
}
