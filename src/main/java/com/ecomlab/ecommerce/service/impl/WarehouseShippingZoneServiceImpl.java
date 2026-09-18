package com.ecomlab.ecommerce.service.impl;

import com.ecomlab.ecommerce.dto.request.WarehouseShippingZoneRequest;
import com.ecomlab.ecommerce.dto.request.WarehouseShippingZoneUpsertRequest;
import com.ecomlab.ecommerce.dto.response.WarehouseShippingZoneResponse;
import com.ecomlab.ecommerce.entity.ShippingZoneEntity;
import com.ecomlab.ecommerce.entity.WarehouseEntity;
import com.ecomlab.ecommerce.entity.WarehouseShippingZoneEntity;
import com.ecomlab.ecommerce.exception.BusinessException;
import com.ecomlab.ecommerce.repository.ShippingZoneRepository;
import com.ecomlab.ecommerce.repository.WarehouseRepository;
import com.ecomlab.ecommerce.repository.WarehouseShippingZoneRepository;
import com.ecomlab.ecommerce.service.WarehouseShippingZoneService;
import com.ecomlab.ecommerce.service.builder.ShippingZoneResponseBuilder;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class WarehouseShippingZoneServiceImpl implements WarehouseShippingZoneService {
  private final WarehouseRepository warehouseRepository;
  private final ShippingZoneRepository shippingZoneRepository;
  private final WarehouseShippingZoneRepository warehouseShippingZoneRepository;

  @Override
  @Transactional(readOnly = true)
  public List<WarehouseShippingZoneResponse> zones(UUID warehouseId) {
    warehouse(warehouseId);

    return warehouseShippingZoneRepository.findByWarehouseId(warehouseId).stream()
        .map(ShippingZoneResponseBuilder::build)
        .toList();
  }

  @Override
  @Transactional
  public List<WarehouseShippingZoneResponse> replace(
      UUID warehouseId, WarehouseShippingZoneRequest request) {
    WarehouseEntity warehouse = warehouse(warehouseId);
    Set<UUID> requestedZoneIds = new HashSet<>();

    for (WarehouseShippingZoneUpsertRequest zoneRequest : request.getZones()) {
      if (!requestedZoneIds.add(zoneRequest.getShippingZoneId())) {
        throw new BusinessException(
            "DUPLICATED_SHIPPING_ZONE",
            "Shipping zone is duplicated in request",
            HttpStatus.BAD_REQUEST);
      }

      ShippingZoneEntity shippingZone = shippingZone(zoneRequest.getShippingZoneId());
      WarehouseShippingZoneEntity mapping =
          warehouseShippingZoneRepository
              .findByWarehouseIdAndShippingZoneId(warehouseId, zoneRequest.getShippingZoneId())
              .orElseGet(WarehouseShippingZoneEntity::new);

      mapping.setWarehouse(warehouse);
      mapping.setShippingZone(shippingZone);
      mapping.setPriority(zoneRequest.getPriority());
      mapping.setActive(zoneRequest.isActive());
      mapping.setDeleted(false);
      warehouseShippingZoneRepository.save(mapping);
    }

    warehouseShippingZoneRepository.findByWarehouseId(warehouseId).stream()
        .filter(mapping -> !requestedZoneIds.contains(mapping.getShippingZone().getId()))
        .forEach(WarehouseShippingZoneEntity::softDelete);

    return zones(warehouseId);
  }

  private WarehouseEntity warehouse(UUID id) {
    return warehouseRepository
        .findByIdAndIsDeletedFalse(id)
        .orElseThrow(
            () ->
                new BusinessException(
                    "WAREHOUSE_NOT_FOUND", "Warehouse not found", HttpStatus.NOT_FOUND));
  }

  private ShippingZoneEntity shippingZone(UUID id) {
    return shippingZoneRepository
        .findByIdAndIsDeletedFalse(id)
        .orElseThrow(
            () ->
                new BusinessException(
                    "SHIPPING_ZONE_NOT_FOUND", "Shipping zone not found", HttpStatus.NOT_FOUND));
  }
}
