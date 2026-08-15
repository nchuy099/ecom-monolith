package com.ecomlab.ecommerce.service;

import com.ecomlab.ecommerce.dto.response.ShipmentResponse;
import java.util.UUID;

public interface ShipmentAdminService {
  ShipmentResponse assign(UUID shipmentId, UUID shipperId);

  ShipmentResponse readyForPickup(UUID shipmentId);
}
