package com.ecomlab.ecommerce.service.impl;

import com.ecomlab.ecommerce.common.enums.Role;
import com.ecomlab.ecommerce.common.enums.ShipmentStatus;
import com.ecomlab.ecommerce.dto.response.ShipmentResponse;
import com.ecomlab.ecommerce.entity.ShipmentEntity;
import com.ecomlab.ecommerce.entity.UserEntity;
import com.ecomlab.ecommerce.exception.BusinessException;
import com.ecomlab.ecommerce.repository.ShipmentRepository;
import com.ecomlab.ecommerce.repository.UserRepository;
import com.ecomlab.ecommerce.service.ShipmentAdminService;
import com.ecomlab.ecommerce.service.builder.ShipmentResponseBuilder;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ShipmentAdminServiceImpl implements ShipmentAdminService {
  private final ShipmentRepository shipmentRepository;
  private final UserRepository userRepository;

  @Override
  @Transactional
  public ShipmentResponse assign(UUID shipmentId, UUID shipperId) {
    ShipmentEntity shipment = shipment(shipmentId);
    UserEntity shipper =
        userRepository
            .findById(shipperId)
            .filter(user -> user.getRole() == Role.SHIPPER && !user.isDeleted())
            .orElseThrow(
                () ->
                    new BusinessException(
                        "SHIPPER_NOT_FOUND", "Shipper not found", HttpStatus.NOT_FOUND));

    if (shipment.getStatus() == ShipmentStatus.DELIVERED
        || shipment.getStatus() == ShipmentStatus.CANCELLED) {
      throw new BusinessException(
          "SHIPMENT_CANNOT_ASSIGN", "Shipment cannot be assigned", HttpStatus.CONFLICT);
    }

    shipment.setShipper(shipper);
    return ShipmentResponseBuilder.build(shipment);
  }

  @Override
  @Transactional
  public ShipmentResponse readyForPickup(UUID shipmentId) {
    ShipmentEntity shipment = shipment(shipmentId);
    if (shipment.getStatus() != ShipmentStatus.PENDING_PACKING) {
      throw new BusinessException(
          "INVALID_SHIPMENT_TRANSITION", "Shipment is not pending packing", HttpStatus.CONFLICT);
    }

    shipment.setStatus(ShipmentStatus.READY_FOR_PICKUP);
    return ShipmentResponseBuilder.build(shipment);
  }

  private ShipmentEntity shipment(UUID shipmentId) {
    return shipmentRepository
        .findById(shipmentId)
        .filter(shipment -> !shipment.isDeleted())
        .orElseThrow(
            () ->
                new BusinessException(
                    "SHIPMENT_NOT_FOUND", "Shipment not found", HttpStatus.NOT_FOUND));
  }
}
