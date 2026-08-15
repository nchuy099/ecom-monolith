package com.ecomlab.ecommerce.service.impl;

import com.ecomlab.ecommerce.common.enums.*;
import com.ecomlab.ecommerce.common.util.Haversine;
import com.ecomlab.ecommerce.dto.response.CheckoutResponse;
import com.ecomlab.ecommerce.entity.*;
import com.ecomlab.ecommerce.exception.BusinessException;
import com.ecomlab.ecommerce.repository.*;
import com.ecomlab.ecommerce.service.CheckoutService;
import com.ecomlab.ecommerce.service.NotificationService;
import com.ecomlab.ecommerce.service.PaymentService;
import com.ecomlab.ecommerce.service.builder.CheckoutResponseBuilder;
import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CheckoutServiceImpl implements CheckoutService {
  private final CartRepository cartRepository;
  private final AddressRepository addressRepository;
  private final WarehouseRepository warehouseRepository;
  private final InventoryRepository inventoryRepository;
  private final OrderRepository orderRepository;
  private final ShipmentRepository shipmentRepository;
  private final ShipmentItemRepository shipmentItemRepository;
  private final PaymentService paymentService;
  private final NotificationService notificationService;

  @Transactional
  public CheckoutResponse checkout(UUID userId, UUID addressId) {
    UserAddressEntity address =
        addressRepository
            .findById(addressId)
            .filter(a -> !a.isDeleted() && a.getUser().getId().equals(userId))
            .orElseThrow(
                () ->
                    new BusinessException(
                        "USER_ADDRESS_NOT_FOUND", "Address not found", HttpStatus.NOT_FOUND));
    CartEntity cart =
        cartRepository
            .findCurrentByUserId(userId)
            .orElseThrow(
                () ->
                    new BusinessException(
                        "CART_NOT_FOUND", "CartEntity not found", HttpStatus.NOT_FOUND));
    if (cart.getItems().isEmpty())
      throw new BusinessException("CART_EMPTY", "CartEntity is empty", HttpStatus.BAD_REQUEST);
    Map<UUID, Integer> quantities =
        cart.getItems().stream()
            .collect(
                Collectors.toMap(
                    i -> i.getVariant().getId(),
                    i -> i.getQuantity(),
                    Integer::sum,
                    LinkedHashMap::new));
    List<WarehouseEntity> ranked =
        warehouseRepository.findActive().stream()
            .sorted(
                Comparator.comparingDouble(
                        (WarehouseEntity w) ->
                            Haversine.kilometers(
                                address.getLatitude(),
                                address.getLongitude(),
                                w.getLatitude(),
                                w.getLongitude()))
                    .thenComparing(WarehouseEntity::getId))
            .toList();
    if (ranked.isEmpty())
      throw new BusinessException(
          "WAREHOUSE_NOT_FOUND", "No active warehouse", HttpStatus.CONFLICT);

    Map<WarehouseEntity, Map<UUID, Allocation>> allocation = allocate(ranked, quantities);
    OrderEntity order = createOrder(cart, address);
    orderRepository.saveAndFlush(order);
    Map<UUID, OrderItemEntity> orderItems =
        order.getItems().stream().collect(Collectors.toMap(i -> i.getVariant().getId(), i -> i));
    for (var warehouseEntry : allocation.entrySet()) {
      ShipmentEntity shipment = new ShipmentEntity();
      shipment.setOrder(order);
      shipment.setWarehouse(warehouseEntry.getKey());
      shipment.setStatus(ShipmentStatus.PENDING_PACKING);
      shipmentRepository.save(shipment);
      List<ShipmentItemEntity> items = new ArrayList<>();
      for (Allocation a : warehouseEntry.getValue().values()) {
        ShipmentItemEntity item = new ShipmentItemEntity();
        item.setShipment(shipment);
        item.setOrderItem(orderItems.get(a.variantId()));
        item.setInventory(a.inventory());
        item.setQuantity(a.quantity());
        items.add(item);
      }
      shipmentItemRepository.saveAll(items);
    }
    cart.getItems().clear();
    paymentService.createPending(order, "COD", "checkout-" + order.getOrderNumber());
    notificationService.queueOrderCreated(order);
    return CheckoutResponseBuilder.build(order, address, shipmentPlan(allocation));
  }

  private Map<WarehouseEntity, Map<UUID, Allocation>> allocate(
      List<WarehouseEntity> ranked, Map<UUID, Integer> quantities) {
    // First pass preserves the single nearest warehouse preference for the entire order.
    for (WarehouseEntity warehouse : ranked) {
      Map<UUID, InventoryEntity> stock = lockedStock(warehouse, quantities.keySet());
      if (quantities.entrySet().stream()
          .allMatch(
              q ->
                  stock.containsKey(q.getKey())
                      && stock.get(q.getKey()).getAvailableQuantity() >= q.getValue())) {
        Map<UUID, Allocation> one = new LinkedHashMap<>();
        quantities.forEach(
            (variant, quantity) -> {
              InventoryEntity i = stock.get(variant);
              i.reserve(quantity);
              one.put(variant, new Allocation(variant, quantity, i));
            });
        return Map.of(warehouse, one);
      }
    }
    Map<WarehouseEntity, Map<UUID, Allocation>> result = new LinkedHashMap<>();
    for (var request : quantities.entrySet()) {
      int remaining = request.getValue();
      for (WarehouseEntity warehouse : ranked) {
        InventoryEntity inventory =
            lockedStock(warehouse, List.of(request.getKey())).get(request.getKey());
        if (inventory == null || inventory.getAvailableQuantity() == 0) continue;
        int take = Math.min(remaining, inventory.getAvailableQuantity());
        inventory.reserve(take);
        result
            .computeIfAbsent(warehouse, ignored -> new LinkedHashMap<>())
            .put(request.getKey(), new Allocation(request.getKey(), take, inventory));
        remaining -= take;
        if (remaining == 0) break;
      }
      if (remaining > 0)
        throw new BusinessException(
            "INSUFFICIENT_INVENTORY",
            "Insufficient stock for variant " + request.getKey(),
            HttpStatus.CONFLICT);
    }
    return result;
  }

  private Map<UUID, InventoryEntity> lockedStock(
      WarehouseEntity warehouse, Collection<UUID> variantIds) {
    return inventoryRepository.lockByWarehouseAndVariants(warehouse.getId(), variantIds).stream()
        .collect(Collectors.toMap(i -> i.getVariant().getId(), i -> i));
  }

  private OrderEntity createOrder(CartEntity cart, UserAddressEntity a) {
    OrderEntity order = new OrderEntity();
    order.setOrderNumber("ORD-" + UUID.randomUUID().toString().substring(0, 12).toUpperCase());
    order.setUser(cart.getUser());
    order.setStatus(OrderStatus.PENDING_PAYMENT);
    order.setRecipientName(a.getRecipientName());
    order.setPhone(a.getPhone());
    order.setAddressLine(a.getAddressLine());
    order.setCity(a.getCity());
    order.setLatitude(a.getLatitude());
    order.setLongitude(a.getLongitude());
    order.setTotalAmount(
        cart.getItems().stream()
            .map(i -> i.getVariant().getPrice().multiply(BigDecimal.valueOf(i.getQuantity())))
            .reduce(BigDecimal.ZERO, BigDecimal::add));
    for (CartItemEntity c : cart.getItems()) {
      OrderItemEntity i = new OrderItemEntity();
      i.setOrder(order);
      i.setVariant(c.getVariant());
      i.setSkuSnapshot(c.getVariant().getSku());
      i.setNameSnapshot(c.getVariant().getName());
      i.setUnitPrice(c.getVariant().getPrice());
      i.setQuantity(c.getQuantity());
      order.getItems().add(i);
    }
    return order;
  }

  private Map<WarehouseEntity, Map<UUID, Integer>> shipmentPlan(
      Map<WarehouseEntity, Map<UUID, Allocation>> allocation) {
    Map<WarehouseEntity, Map<UUID, Integer>> result = new LinkedHashMap<>();

    for (var warehouseEntry : allocation.entrySet()) {
      Map<UUID, Integer> lines = new LinkedHashMap<>();
      warehouseEntry
          .getValue()
          .forEach((variantId, value) -> lines.put(variantId, value.quantity()));
      result.put(warehouseEntry.getKey(), lines);
    }

    return result;
  }

  private record Allocation(UUID variantId, int quantity, InventoryEntity inventory) {}
}
