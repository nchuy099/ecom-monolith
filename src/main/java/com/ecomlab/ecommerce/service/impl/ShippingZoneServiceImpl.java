package com.ecomlab.ecommerce.service.impl;

import com.ecomlab.ecommerce.dto.request.ShippingZoneRequest;
import com.ecomlab.ecommerce.dto.response.ShippingZoneResponse;
import com.ecomlab.ecommerce.entity.ShippingZoneEntity;
import com.ecomlab.ecommerce.exception.BusinessException;
import com.ecomlab.ecommerce.repository.ShippingZoneRepository;
import com.ecomlab.ecommerce.service.ShippingZoneService;
import com.ecomlab.ecommerce.service.builder.ShippingZoneResponseBuilder;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ShippingZoneServiceImpl implements ShippingZoneService {
  private final ShippingZoneRepository shippingZoneRepository;

  @Override
  @Transactional(readOnly = true)
  public List<ShippingZoneResponse> shippingZones() {
    return shippingZoneRepository.findByIsDeletedFalseOrderByNameAsc().stream()
        .map(ShippingZoneResponseBuilder::build)
        .toList();
  }

  @Override
  @Transactional
  public ShippingZoneResponse create(ShippingZoneRequest request) {
    if (shippingZoneRepository.existsByCodeAndIsDeletedFalse(request.getCode())) {
      throw new BusinessException(
          "SHIPPING_ZONE_CODE_ALREADY_EXISTS",
          "Shipping zone code already exists",
          HttpStatus.CONFLICT);
    }

    ShippingZoneEntity shippingZone = new ShippingZoneEntity();
    apply(shippingZone, request);

    return ShippingZoneResponseBuilder.build(shippingZoneRepository.save(shippingZone));
  }

  @Override
  @Transactional
  public ShippingZoneResponse update(UUID id, ShippingZoneRequest request) {
    ShippingZoneEntity shippingZone = shippingZone(id);
    apply(shippingZone, request);

    return ShippingZoneResponseBuilder.build(shippingZone);
  }

  @Override
  @Transactional
  public void delete(UUID id) {
    shippingZone(id).softDelete();
  }

  private ShippingZoneEntity shippingZone(UUID id) {
    return shippingZoneRepository
        .findByIdAndIsDeletedFalse(id)
        .orElseThrow(
            () ->
                new BusinessException(
                    "SHIPPING_ZONE_NOT_FOUND", "Shipping zone not found", HttpStatus.NOT_FOUND));
  }

  private void apply(ShippingZoneEntity shippingZone, ShippingZoneRequest request) {
    shippingZone.setCode(request.getCode());
    shippingZone.setName(request.getName());
    shippingZone.setMatchedCities(String.join(",", request.getMatchedCities()));
    shippingZone.setActive(true);
  }
}
