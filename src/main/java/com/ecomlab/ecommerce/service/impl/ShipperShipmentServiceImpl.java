package com.ecomlab.ecommerce.service.impl;

import com.ecomlab.ecommerce.common.enums.OrderStatus;
import com.ecomlab.ecommerce.common.enums.Role;
import com.ecomlab.ecommerce.common.enums.ShipmentStatus;
import com.ecomlab.ecommerce.dto.response.ShipmentResponse;
import com.ecomlab.ecommerce.entity.ShipmentEntity;
import com.ecomlab.ecommerce.entity.TrackingEventEntity;
import com.ecomlab.ecommerce.entity.UserEntity;
import com.ecomlab.ecommerce.exception.BusinessException;
import com.ecomlab.ecommerce.repository.ShipmentRepository;
import com.ecomlab.ecommerce.repository.TrackingEventRepository;
import com.ecomlab.ecommerce.repository.UserRepository;
import com.ecomlab.ecommerce.service.NotificationService;
import com.ecomlab.ecommerce.service.ShipperShipmentService;
import com.ecomlab.ecommerce.service.builder.ShipmentResponseBuilder;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.EnumSet;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ShipperShipmentServiceImpl implements ShipperShipmentService {
  private static final ZoneId BUSINESS_ZONE = ZoneId.of("Asia/Ho_Chi_Minh");
  private static final EnumSet<ShipmentStatus> TODAY_ACTIVE_STATUSES =
      EnumSet.of(
          ShipmentStatus.PENDING_PACKING,
          ShipmentStatus.READY_FOR_PICKUP,
          ShipmentStatus.PICKED_UP,
          ShipmentStatus.IN_TRANSIT,
          ShipmentStatus.OUT_FOR_DELIVERY);

  private final ShipmentRepository shipmentRepository;
  private final UserRepository userRepository;
  private final TrackingEventRepository trackingEventRepository;
  private final NotificationService notificationService;

  @Override
  @Transactional
  public void assign(UUID shipmentId, UUID shipperId) {
    ShipmentEntity shipment =
        shipmentRepository
            .findById(shipmentId)
            .orElseThrow(
                () ->
                    new BusinessException(
                        "SHIPMENT_NOT_FOUND", "Shipment not found", HttpStatus.NOT_FOUND));

    UserEntity shipper =
        userRepository
            .findById(shipperId)
            .orElseThrow(
                () ->
                    new BusinessException(
                        "SHIPPER_NOT_FOUND", "Shipper not found", HttpStatus.NOT_FOUND));

    if (shipper.getRole() != Role.SHIPPER) {
      throw new BusinessException(
          "USER_IS_NOT_SHIPPER", "User is not a shipper", HttpStatus.BAD_REQUEST);
    }

    if (shipment.getStatus() != ShipmentStatus.READY_FOR_PICKUP) {
      throw new BusinessException(
          "SHIPMENT_NOT_READY", "Shipment must be ready for pickup", HttpStatus.CONFLICT);
    }

    shipment.setShipper(shipper);
  }

  @Override
  @Transactional(readOnly = true)
  public List<ShipmentResponse> getAssignedShipments(UUID shipperId) {
    return shipmentRepository.findAssignedByShipperId(shipperId).stream()
        .map(ShipmentResponseBuilder::build)
        .toList();
  }

  @Override
  @Transactional(readOnly = true)
  public List<ShipmentResponse> getTodayShipments(UUID shipperId) {
    return shipmentRepository
        .findTodayByShipperId(
            shipperId, TODAY_ACTIVE_STATUSES, ShipmentStatus.DELIVERED, startOfBusinessDay())
        .stream()
        .map(ShipmentResponseBuilder::build)
        .toList();
  }

  @Override
  @Transactional(readOnly = true)
  public ShipmentResponse findByTrackingNumber(UUID shipperId, String trackingNumber) {
    String normalizedTrackingNumber = normalizeTrackingNumber(trackingNumber);

    ShipmentEntity shipment =
        shipmentRepository
            .findAssignedByTrackingNumber(shipperId, normalizedTrackingNumber)
            .orElseThrow(
                () ->
                    new BusinessException(
                        "SHIPMENT_NOT_FOUND",
                        "Shipment not found for current shipper",
                        HttpStatus.NOT_FOUND));

    return ShipmentResponseBuilder.build(shipment);
  }

  @Override
  @Transactional
  public void updateTracking(
      UUID shipmentId,
      UUID shipperId,
      String nextStatus,
      String note,
      BigDecimal latitude,
      BigDecimal longitude,
      String proofUrl) {
    ShipmentStatus next = shipmentStatus(nextStatus);
    ShipmentEntity shipment = shipment(shipmentId);

    if (shipment.getShipper() == null || !shipment.getShipper().getId().equals(shipperId)) {
      throw new BusinessException(
          "SHIPMENT_NOT_ASSIGNED_TO_SHIPPER",
          "Shipment is not assigned to this shipper",
          HttpStatus.FORBIDDEN);
    }

    if (!allowed(shipment.getStatus(), next)) {
      throw new BusinessException(
          "INVALID_SHIPMENT_TRANSITION", "Invalid shipment transition", HttpStatus.CONFLICT);
    }

    boolean deliveredNow = next == ShipmentStatus.DELIVERED;
    shipment.setStatus(next);
    if (deliveredNow) {
      consumeReservedStock(shipment);
    }
    trackingEventRepository.save(
        trackingEvent(shipment, next, note, latitude, longitude, proofUrl));
    completeOrderWhenAllShipmentsDelivered(shipment);
  }

  private void consumeReservedStock(ShipmentEntity shipment) {
    shipment.getItems().forEach(item -> item.getInventory().consumeReserved(item.getQuantity()));
  }

  private void completeOrderWhenAllShipmentsDelivered(ShipmentEntity shipment) {
    if (shipment.getStatus() != ShipmentStatus.DELIVERED) {
      return;
    }

    boolean allDelivered =
        shipmentRepository.findActiveByOrderId(shipment.getOrder().getId()).stream()
            .allMatch(item -> item.getStatus() == ShipmentStatus.DELIVERED);
    if (!allDelivered || shipment.getOrder().getStatus() == OrderStatus.COMPLETED) {
      return;
    }

    shipment.getOrder().setStatus(OrderStatus.COMPLETED);
    notificationService.queueShipmentDelivered(shipment.getOrder());
  }

  private ShipmentEntity shipment(UUID shipmentId) {
    return shipmentRepository
        .findByIdWithItems(shipmentId)
        .orElseThrow(
            () ->
                new BusinessException(
                    "SHIPMENT_NOT_FOUND", "Shipment not found", HttpStatus.NOT_FOUND));
  }

  private ShipmentStatus shipmentStatus(String value) {
    try {
      return ShipmentStatus.valueOf(value);
    } catch (IllegalArgumentException exception) {
      throw new BusinessException(
          "INVALID_SHIPMENT_STATUS", "Unknown shipment status", HttpStatus.BAD_REQUEST);
    }
  }

  private Instant startOfBusinessDay() {
    return LocalDate.now(BUSINESS_ZONE).atStartOfDay(BUSINESS_ZONE).toInstant();
  }

  private String normalizeTrackingNumber(String trackingNumber) {
    if (trackingNumber == null || trackingNumber.isBlank()) {
      throw new BusinessException(
          "TRACKING_NUMBER_REQUIRED", "Tracking number is required", HttpStatus.BAD_REQUEST);
    }

    return trackingNumber.trim();
  }

  private TrackingEventEntity trackingEvent(
      ShipmentEntity shipment,
      ShipmentStatus status,
      String note,
      BigDecimal latitude,
      BigDecimal longitude,
      String proofUrl) {
    TrackingEventEntity event = new TrackingEventEntity();
    event.setShipment(shipment);
    event.setActor(shipment.getShipper());
    event.setStatus(status);
    event.setNote(note);
    event.setLatitude(latitude);
    event.setLongitude(longitude);
    event.setProofUrl(proofUrl);
    event.setOccurredAt(Instant.now());
    return event;
  }

  private boolean allowed(ShipmentStatus from, ShipmentStatus to) {
    return switch (from) {
      case PENDING_PACKING -> to == ShipmentStatus.OUT_FOR_DELIVERY;
      case READY_FOR_PICKUP ->
          to == ShipmentStatus.PICKED_UP || to == ShipmentStatus.OUT_FOR_DELIVERY;
      case PICKED_UP -> to == ShipmentStatus.IN_TRANSIT;
      case IN_TRANSIT ->
          to == ShipmentStatus.OUT_FOR_DELIVERY || to == ShipmentStatus.DELIVERY_FAILED;
      case OUT_FOR_DELIVERY ->
          to == ShipmentStatus.DELIVERED || to == ShipmentStatus.DELIVERY_FAILED;
      default -> false;
    };
  }
}
