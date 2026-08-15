package com.ecomlab.ecommerce.service;

import com.ecomlab.ecommerce.dto.request.WarehouseRequest;
import com.ecomlab.ecommerce.dto.response.WarehouseResponse;
import java.util.List;
import java.util.UUID;

public interface WarehouseService {
  List<WarehouseResponse> warehouses();

  WarehouseResponse create(WarehouseRequest request);

  WarehouseResponse update(UUID id, WarehouseRequest request);

  void delete(UUID id);
}
