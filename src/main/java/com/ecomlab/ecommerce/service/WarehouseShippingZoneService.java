package com.ecomlab.ecommerce.service;

import com.ecomlab.ecommerce.dto.request.WarehouseShippingZoneRequest;
import com.ecomlab.ecommerce.dto.response.WarehouseShippingZoneResponse;
import java.util.List;
import java.util.UUID;

public interface WarehouseShippingZoneService {
  List<WarehouseShippingZoneResponse> zones(UUID warehouseId);

  List<WarehouseShippingZoneResponse> replace(
      UUID warehouseId, WarehouseShippingZoneRequest request);
}
