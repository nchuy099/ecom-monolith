package com.ecomlab.ecommerce.service.impl;

import com.ecomlab.ecommerce.dto.response.ShipmentResponse;
import com.ecomlab.ecommerce.entity.ShipmentEntity;
import com.ecomlab.ecommerce.entity.TrackingEventEntity;
import com.ecomlab.ecommerce.repository.ShipmentRepository;
import com.ecomlab.ecommerce.repository.TrackingEventRepository;
import com.ecomlab.ecommerce.service.CustomerShipmentService;
import com.ecomlab.ecommerce.service.builder.ShipmentResponseBuilder;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CustomerShipmentServiceImpl implements CustomerShipmentService {
  private final ShipmentRepository shipmentRepository;
  private final TrackingEventRepository trackingEventRepository;

  @Transactional(readOnly = true)
  public List<ShipmentResponse> getMyShipments(UUID userId) {
    return buildWithTimeline(shipmentRepository.findAllByCustomerId(userId));
  }

  @Transactional(readOnly = true)
  public List<ShipmentResponse> getMyOrderShipments(UUID userId, UUID orderId) {
    return buildWithTimeline(shipmentRepository.findOwnedByOrderId(userId, orderId));
  }

  private List<ShipmentResponse> buildWithTimeline(List<ShipmentEntity> shipments) {
    if (shipments.isEmpty()) {
      return List.of();
    }

    Map<UUID, List<TrackingEventEntity>> eventsByShipmentId =
        trackingEventRepository
            .findByShipmentIds(shipments.stream().map(ShipmentEntity::getId).toList())
            .stream()
            .collect(Collectors.groupingBy(event -> event.getShipment().getId()));

    return shipments.stream()
        .map(shipment -> ShipmentResponseBuilder.build(shipment, eventsByShipmentId))
        .toList();
  }
}
