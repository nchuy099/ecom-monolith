package com.ecomlab.ecommerce.service;

import com.ecomlab.ecommerce.dto.response.ShipmentResponse;
import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public interface ShipperShipmentService {
  void assign(UUID shipmentId, UUID shipperId);

  List<ShipmentResponse> getAssignedShipments(UUID shipperId);

  List<ShipmentResponse> getTodayShipments(UUID shipperId);

  ShipmentResponse findByTrackingNumber(UUID shipperId, String trackingNumber);

  void updateTracking(
      UUID shipmentId,
      UUID shipperId,
      String status,
      String note,
      BigDecimal latitude,
      BigDecimal longitude,
      String proofUrl);
}
