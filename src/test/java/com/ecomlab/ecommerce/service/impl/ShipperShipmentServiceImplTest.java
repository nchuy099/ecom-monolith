package com.ecomlab.ecommerce.service.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.ecomlab.ecommerce.common.enums.OrderStatus;
import com.ecomlab.ecommerce.common.enums.Role;
import com.ecomlab.ecommerce.common.enums.ShipmentStatus;
import com.ecomlab.ecommerce.entity.InventoryEntity;
import com.ecomlab.ecommerce.entity.OrderEntity;
import com.ecomlab.ecommerce.entity.ShipmentEntity;
import com.ecomlab.ecommerce.entity.ShipmentItemEntity;
import com.ecomlab.ecommerce.entity.UserEntity;
import com.ecomlab.ecommerce.repository.ShipmentRepository;
import com.ecomlab.ecommerce.repository.TrackingEventRepository;
import com.ecomlab.ecommerce.repository.UserRepository;
import com.ecomlab.ecommerce.service.NotificationService;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ShipperShipmentServiceImplTest {
  @Mock private ShipmentRepository shipmentRepository;
  @Mock private UserRepository userRepository;
  @Mock private TrackingEventRepository trackingEventRepository;
  @Mock private NotificationService notificationService;

  @Test
  void delivered_shipment_consumes_reserved_stock_without_returning_available_quantity() {
    ShipperShipmentServiceImpl service =
        new ShipperShipmentServiceImpl(
            shipmentRepository, userRepository, trackingEventRepository, notificationService);
    UUID shipmentId = UUID.randomUUID();
    UUID shipperId = UUID.randomUUID();
    UserEntity shipper = new UserEntity();
    shipper.setId(shipperId);
    shipper.setRole(Role.SHIPPER);

    InventoryEntity inventory = new InventoryEntity();
    inventory.setAvailableQuantity(19);
    inventory.setReservedQuantity(2);

    OrderEntity order = new OrderEntity();
    order.setId(UUID.randomUUID());
    order.setStatus(OrderStatus.CONFIRMED);

    ShipmentEntity shipment = new ShipmentEntity();
    shipment.setId(shipmentId);
    shipment.setOrder(order);
    shipment.setShipper(shipper);
    shipment.setStatus(ShipmentStatus.OUT_FOR_DELIVERY);

    ShipmentItemEntity item = new ShipmentItemEntity();
    item.setShipment(shipment);
    item.setInventory(inventory);
    item.setQuantity(2);
    shipment.setItems(List.of(item));

    when(shipmentRepository.findByIdWithItems(shipmentId)).thenReturn(Optional.of(shipment));
    when(shipmentRepository.findActiveByOrderId(order.getId())).thenReturn(List.of(shipment));

    service.updateTracking(shipmentId, shipperId, "DELIVERED", "Giao thành công", null, null, null);

    assertThat(inventory.getAvailableQuantity()).isEqualTo(19);
    assertThat(inventory.getReservedQuantity()).isZero();
    assertThat(shipment.getStatus()).isEqualTo(ShipmentStatus.DELIVERED);
    assertThat(order.getStatus()).isEqualTo(OrderStatus.COMPLETED);
    verify(trackingEventRepository).save(any());
    verify(notificationService).queueShipmentDelivered(order);
  }
}
