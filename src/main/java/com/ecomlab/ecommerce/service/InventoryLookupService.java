package com.ecomlab.ecommerce.service;

import com.ecomlab.ecommerce.dto.response.InventoryResponse;
import java.util.List;
import java.util.UUID;

public interface InventoryLookupService {
  List<InventoryResponse> warehousesByVariant(UUID userId, UUID variantId, UUID addressId);
}
