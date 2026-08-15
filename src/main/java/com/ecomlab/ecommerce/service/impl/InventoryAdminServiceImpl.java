package com.ecomlab.ecommerce.service.impl;

import com.ecomlab.ecommerce.dto.request.AdjustInventoryRequest;
import com.ecomlab.ecommerce.dto.request.UpsertInventoryRequest;
import com.ecomlab.ecommerce.dto.response.InventoryResponse;
import com.ecomlab.ecommerce.entity.InventoryEntity;
import com.ecomlab.ecommerce.entity.ProductVariantEntity;
import com.ecomlab.ecommerce.entity.WarehouseEntity;
import com.ecomlab.ecommerce.exception.BusinessException;
import com.ecomlab.ecommerce.repository.InventoryRepository;
import com.ecomlab.ecommerce.repository.ProductVariantRepository;
import com.ecomlab.ecommerce.repository.WarehouseRepository;
import com.ecomlab.ecommerce.service.InventoryAdminService;
import com.ecomlab.ecommerce.service.ProductCatalogService;
import com.ecomlab.ecommerce.service.builder.InventoryResponseBuilder;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class InventoryAdminServiceImpl implements InventoryAdminService {
  private final InventoryRepository inventoryRepository;
  private final WarehouseRepository warehouseRepository;
  private final ProductVariantRepository productVariantRepository;
  private final ProductCatalogService productCatalogService;

  @Transactional(readOnly = true)
  public List<InventoryResponse> list(UUID warehouseId, UUID variantId) {
    List<InventoryEntity> inventories =
        inventoryRepository.findActiveByFilters(warehouseId, variantId);

    return inventories.stream().map(InventoryResponseBuilder::build).toList();
  }

  @Transactional
  public InventoryResponse upsert(UpsertInventoryRequest request) {
    WarehouseEntity warehouse =
        warehouseRepository
            .findById(request.getWarehouseId())
            .filter(value -> !value.isDeleted())
            .orElseThrow(
                () ->
                    new BusinessException(
                        "WAREHOUSE_NOT_FOUND", "WarehouseEntity not found", HttpStatus.NOT_FOUND));
    ProductVariantEntity variant =
        productVariantRepository
            .findById(request.getVariantId())
            .filter(value -> !value.isDeleted())
            .orElseThrow(
                () ->
                    new BusinessException(
                        "PRODUCT_VARIANT_NOT_FOUND",
                        "ProductEntity variant not found",
                        HttpStatus.NOT_FOUND));
    InventoryEntity inventory =
        inventoryRepository
            .findActiveByWarehouseIdAndVariantId(warehouse.getId(), variant.getId())
            .orElseGet(
                () -> {
                  InventoryEntity created = new InventoryEntity();
                  created.setWarehouse(warehouse);
                  created.setVariant(variant);
                  return created;
                });
    if (request.getAvailableQuantity() < inventory.getReservedQuantity()) {
      throw new BusinessException(
          "INVALID_INVENTORY_QUANTITY",
          "Available quantity cannot be below reserved quantity",
          HttpStatus.BAD_REQUEST);
    }
    inventory.setAvailableQuantity(request.getAvailableQuantity());
    InventoryEntity saved = inventoryRepository.save(inventory);
    productCatalogService.evictDetail(variant.getProduct().getId());
    return InventoryResponseBuilder.build(saved);
  }

  @Transactional
  public InventoryResponse adjust(AdjustInventoryRequest request) {
    InventoryEntity inventory =
        inventoryRepository
            .findActiveByWarehouseIdAndVariantId(request.getWarehouseId(), request.getVariantId())
            .orElseThrow(
                () ->
                    new BusinessException(
                        "INVENTORY_NOT_FOUND", "InventoryEntity not found", HttpStatus.NOT_FOUND));
    int next = inventory.getAvailableQuantity() + request.getQuantityDelta();
    if (next < 0) {
      throw new BusinessException(
          "INVALID_INVENTORY_QUANTITY", "InventoryEntity cannot be negative", HttpStatus.CONFLICT);
    }
    inventory.setAvailableQuantity(next);
    productCatalogService.evictDetail(inventory.getVariant().getProduct().getId());
    return InventoryResponseBuilder.build(inventory);
  }
}
