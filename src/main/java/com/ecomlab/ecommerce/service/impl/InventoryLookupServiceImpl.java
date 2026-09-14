package com.ecomlab.ecommerce.service.impl;

import com.ecomlab.ecommerce.dto.response.InventoryResponse;
import com.ecomlab.ecommerce.entity.ShippingZoneEntity;
import com.ecomlab.ecommerce.entity.UserAddressEntity;
import com.ecomlab.ecommerce.exception.BusinessException;
import com.ecomlab.ecommerce.repository.AddressRepository;
import com.ecomlab.ecommerce.repository.InventoryRepository;
import com.ecomlab.ecommerce.repository.ShippingZoneRepository;
import com.ecomlab.ecommerce.repository.WarehouseShippingZoneRepository;
import com.ecomlab.ecommerce.service.InventoryLookupService;
import com.ecomlab.ecommerce.service.builder.InventoryResponseBuilder;
import java.text.Normalizer;
import java.util.*;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class InventoryLookupServiceImpl implements InventoryLookupService {
  private final InventoryRepository inventoryRepository;
  private final AddressRepository addressRepository;
  private final ShippingZoneRepository shippingZoneRepository;
  private final WarehouseShippingZoneRepository warehouseShippingZoneRepository;

  @Override
  @Transactional(readOnly = true)
  public List<InventoryResponse> warehousesByVariant(UUID userId, UUID variantId, UUID addressId) {
    Set<UUID> deliverableWarehouseIds = deliverableWarehouseIds(userId, addressId);

    return inventoryRepository.findActiveByVariantId(variantId).stream()
        .map(
            inventory ->
                InventoryResponseBuilder.build(
                    inventory, deliverableWarehouseIds.contains(inventory.getWarehouse().getId())))
        .toList();
  }

  private Set<UUID> deliverableWarehouseIds(UUID userId, UUID addressId) {
    UserAddressEntity address =
        addressRepository
            .findOwnedById(addressId, userId)
            .orElseThrow(
                () ->
                    new BusinessException(
                        "USER_ADDRESS_NOT_FOUND", "Address not found", HttpStatus.NOT_FOUND));
    ShippingZoneEntity shippingZone = shippingZone(address);

    return warehouseShippingZoneRepository.findActiveByShippingZoneId(shippingZone.getId()).stream()
        .map(mapping -> mapping.getWarehouse().getId())
        .collect(Collectors.toSet());
  }

  private ShippingZoneEntity shippingZone(UserAddressEntity address) {
    String addressCity = normalize(address.getCity());

    return shippingZoneRepository.findByActiveTrueAndIsDeletedFalseOrderByNameAsc().stream()
        .filter(
            zone ->
                Arrays.stream(zone.getMatchedCities().split(","))
                    .map(this::normalize)
                    .anyMatch(city -> city.equals(addressCity)))
        .findFirst()
        .orElseThrow(
            () ->
                new BusinessException(
                    "DELIVERY_ZONE_NOT_SUPPORTED",
                    "Delivery zone is not supported for " + address.getCity(),
                    HttpStatus.CONFLICT));
  }

  private String normalize(String value) {
    if (value == null) {
      return "";
    }

    return Normalizer.normalize(value, Normalizer.Form.NFD)
        .replaceAll("\\p{M}", "")
        .replaceAll("[^A-Za-z0-9]", "")
        .toLowerCase(Locale.ROOT);
  }
}
