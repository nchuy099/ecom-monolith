package com.ecomlab.ecommerce.service.impl;

import com.ecomlab.ecommerce.dto.request.WarehouseRequest;
import com.ecomlab.ecommerce.dto.response.WarehouseResponse;
import com.ecomlab.ecommerce.entity.WarehouseEntity;
import com.ecomlab.ecommerce.exception.BusinessException;
import com.ecomlab.ecommerce.repository.WarehouseRepository;
import com.ecomlab.ecommerce.service.WarehouseService;
import com.ecomlab.ecommerce.service.builder.WarehouseResponseBuilder;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class WarehouseServiceImpl implements WarehouseService {
  private final WarehouseRepository warehouseRepository;

  @Override
  @Transactional(readOnly = true)
  public List<WarehouseResponse> warehouses() {
    List<WarehouseEntity> warehouses = warehouseRepository.findByIsDeletedFalseOrderByNameAsc();

    return warehouses.stream().map(WarehouseResponseBuilder::build).toList();
  }

  @Override
  @Transactional
  public WarehouseResponse create(WarehouseRequest request) {
    if (warehouseRepository.existsByCode(request.code())) {
      throw new BusinessException(
          "WAREHOUSE_CODE_ALREADY_EXISTS", "Warehouse code already exists", HttpStatus.CONFLICT);
    }

    WarehouseEntity warehouse = new WarehouseEntity();
    apply(warehouse, request);

    return WarehouseResponseBuilder.build(warehouseRepository.save(warehouse));
  }

  @Override
  @Transactional
  public WarehouseResponse update(UUID id, WarehouseRequest request) {
    WarehouseEntity warehouse = warehouse(id);
    apply(warehouse, request);
    return WarehouseResponseBuilder.build(warehouse);
  }

  @Override
  @Transactional
  public void delete(UUID id) {
    warehouse(id).softDelete();
  }

  private WarehouseEntity warehouse(UUID id) {
    return warehouseRepository
        .findByIdAndIsDeletedFalse(id)
        .orElseThrow(
            () ->
                new BusinessException(
                    "WAREHOUSE_NOT_FOUND", "Warehouse not found", HttpStatus.NOT_FOUND));
  }

  private void apply(WarehouseEntity warehouse, WarehouseRequest request) {
    warehouse.setCode(request.code());
    warehouse.setName(request.name());
    warehouse.setAddressLine(request.addressLine());
    warehouse.setLatitude(request.latitude());
    warehouse.setLongitude(request.longitude());
  }
}
