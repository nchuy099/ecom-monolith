package com.ecomlab.ecommerce.service;

import com.ecomlab.ecommerce.dto.response.ShipmentResponse;
import java.util.List;
import java.util.UUID;

public interface CustomerShipmentService {
  List<ShipmentResponse> getMyShipments(UUID userId);

  List<ShipmentResponse> getMyOrderShipments(UUID userId, UUID orderId);
}
