package com.ecomlab.ecommerce.service;

import com.ecomlab.ecommerce.dto.response.ShipmentSummaryResponse;
import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public interface ShipperShipmentService {
  void assign(UUID shipmentId, UUID shipperId);

  List<ShipmentSummaryResponse> getAssignedShipments(UUID shipperId);

  void updateTracking(
      UUID shipmentId,
      UUID shipperId,
      String status,
      String note,
      BigDecimal latitude,
      BigDecimal longitude,
      String proofUrl);
}
