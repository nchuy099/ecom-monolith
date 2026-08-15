package com.ecomlab.ecommerce.service;

import com.ecomlab.ecommerce.dto.request.AdjustInventoryRequest;
import com.ecomlab.ecommerce.dto.request.UpsertInventoryRequest;
import com.ecomlab.ecommerce.dto.response.InventoryResponse;
import java.util.List;
import java.util.UUID;

public interface InventoryAdminService {
  List<InventoryResponse> list(UUID warehouseId, UUID variantId);

  InventoryResponse upsert(UpsertInventoryRequest request);

  InventoryResponse adjust(AdjustInventoryRequest request);
}
