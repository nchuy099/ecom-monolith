package com.ecomlab.ecommerce.service.builder;

import com.ecomlab.ecommerce.common.util.Haversine;
import com.ecomlab.ecommerce.dto.response.ShipmentResponse;
import com.ecomlab.ecommerce.dto.response.ShipmentSummaryResponse;
import com.ecomlab.ecommerce.dto.response.TrackingEventResponse;
import com.ecomlab.ecommerce.entity.ShipmentEntity;
import com.ecomlab.ecommerce.entity.TrackingEventEntity;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public final class ShipmentResponseBuilder {
  private ShipmentResponseBuilder() {}

  public static ShipmentResponse build(ShipmentEntity shipment) {
    return build(shipment, List.of());
  }

  public static ShipmentResponse build(
      ShipmentEntity shipment, List<TrackingEventEntity> trackingEvents) {
    return ShipmentResponse.builder()
        .id(shipment.getId())
        .orderId(shipment.getOrder().getId())
        .orderNumber(shipment.getOrder().getOrderNumber())
        .warehouseId(shipment.getWarehouse().getId())
        .warehouseName(shipment.getWarehouse().getName())
        .shipperId(shipment.getShipper() == null ? null : shipment.getShipper().getId())
        .shipperName(shipment.getShipper() == null ? null : shipment.getShipper().getDisplayName())
        .status(shipment.getStatus().name())
        .trackingNumber(shipment.getTrackingNumber())
        .recipientName(shipment.getOrder().getRecipientName())
        .recipientPhone(shipment.getOrder().getPhone())
        .deliveryAddress(
            shipment.getOrder().getAddressLine() + ", " + shipment.getOrder().getCity())
        .deliveryLatitude(shipment.getOrder().getLatitude())
        .deliveryLongitude(shipment.getOrder().getLongitude())
        .warehouseLatitude(shipment.getWarehouse().getLatitude())
        .warehouseLongitude(shipment.getWarehouse().getLongitude())
        .distanceKm(distanceKm(shipment))
        .updatedAt(shipment.getUpdatedAt())
        .timeline(trackingEvents.stream().map(ShipmentResponseBuilder::tracking).toList())
        .build();
  }

  public static ShipmentResponse build(
      ShipmentEntity shipment, Map<UUID, List<TrackingEventEntity>> eventsByShipmentId) {
    return build(shipment, eventsByShipmentId.getOrDefault(shipment.getId(), List.of()));
  }

  public static ShipmentSummaryResponse summary(ShipmentEntity shipment) {
    return ShipmentSummaryResponse.builder()
        .id(shipment.getId())
        .status(shipment.getStatus().name())
        .trackingNumber(shipment.getTrackingNumber())
        .warehouseName(shipment.getWarehouse().getName())
        .warehouseAddress(shipment.getWarehouse().getAddressLine())
        .recipientName(shipment.getOrder().getRecipientName())
        .recipientPhone(shipment.getOrder().getPhone())
        .deliveryAddress(
            shipment.getOrder().getAddressLine() + ", " + shipment.getOrder().getCity())
        .build();
  }

  private static TrackingEventResponse tracking(TrackingEventEntity event) {
    return TrackingEventResponse.builder()
        .status(event.getStatus().name())
        .timestamp(event.getOccurredAt())
        .location(event.getShipment().getWarehouse().getName())
        .note(event.getNote())
        .build();
  }

  private static double distanceKm(ShipmentEntity shipment) {
    double distance =
        Haversine.kilometers(
            shipment.getOrder().getLatitude(),
            shipment.getOrder().getLongitude(),
            shipment.getWarehouse().getLatitude(),
            shipment.getWarehouse().getLongitude());
    return Math.round(distance * 10.0) / 10.0;
  }
}
