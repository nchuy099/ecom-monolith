package com.ecomlab.ecommerce.service.impl;

import com.ecomlab.ecommerce.common.enums.*;
import com.ecomlab.ecommerce.common.util.Haversine;
import com.ecomlab.ecommerce.dto.response.CheckoutItemAvailabilityResponse;
import com.ecomlab.ecommerce.dto.response.CheckoutResponse;
import com.ecomlab.ecommerce.entity.*;
import com.ecomlab.ecommerce.exception.BusinessException;
import com.ecomlab.ecommerce.repository.*;
import com.ecomlab.ecommerce.service.CheckoutService;
import com.ecomlab.ecommerce.service.NotificationService;
import com.ecomlab.ecommerce.service.PaymentService;
import com.ecomlab.ecommerce.service.builder.CheckoutResponseBuilder;
import java.math.BigDecimal;
import java.text.Normalizer;
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
  private final ShippingZoneRepository shippingZoneRepository;
  private final WarehouseShippingZoneRepository warehouseShippingZoneRepository;
  private final InventoryRepository inventoryRepository;
  private final OrderRepository orderRepository;
  private final ShipmentRepository shipmentRepository;
  private final ShipmentItemRepository shipmentItemRepository;
  private final TrackingEventRepository trackingEventRepository;
  private final PaymentService paymentService;
  private final NotificationService notificationService;

  @Transactional(readOnly = true)
  public CheckoutResponse preview(UUID userId, UUID addressId) {
    UserAddressEntity address = address(userId, addressId);
    CartEntity cart = cart(userId);
    Map<UUID, Integer> quantities = quantities(cart);
    List<WarehouseEntity> ranked = rankedWarehouses(address);
    Map<UUID, Integer> availableQuantities = availableQuantities(ranked, quantities.keySet());
    List<CheckoutItemAvailabilityResponse> items =
        CheckoutResponseBuilder.itemAvailability(cart.getItems(), availableQuantities);
    boolean hasEnoughStock = items.stream().allMatch(CheckoutItemAvailabilityResponse::isAvailable);
    Map<WarehouseEntity, Map<UUID, Allocation>> allocation =
        hasEnoughStock ? allocate(ranked, quantities, false) : Map.of();

    return CheckoutResponseBuilder.preview(address, shipmentPlan(allocation), items);
  }

  @Transactional
  public CheckoutResponse checkout(UUID userId, UUID addressId) {
    UserAddressEntity address = address(userId, addressId);
    CartEntity cart = cart(userId);
    Map<UUID, Integer> quantities = quantities(cart);
    List<WarehouseEntity> ranked = rankedWarehouses(address);
    Map<WarehouseEntity, Map<UUID, Allocation>> allocation = allocate(ranked, quantities, true);
    List<CheckoutItemAvailabilityResponse> checkoutItems =
        CheckoutResponseBuilder.itemAvailability(cart.getItems(), quantities);
    OrderEntity order = createOrder(cart, address);
    orderRepository.saveAndFlush(order);
    Map<UUID, OrderItemEntity> orderItems =
        order.getItems().stream().collect(Collectors.toMap(i -> i.getVariant().getId(), i -> i));
    for (var warehouseEntry : allocation.entrySet()) {
      ShipmentEntity shipment = new ShipmentEntity();
      shipment.setOrder(order);
      shipment.setWarehouse(warehouseEntry.getKey());
      shipment.setTrackingNumber(trackingNumber(warehouseEntry.getKey()));
      shipment.setStatus(ShipmentStatus.PENDING_PACKING);
      shipmentRepository.save(shipment);
      List<ShipmentItemEntity> shipmentItems = new ArrayList<>();
      for (Allocation a : warehouseEntry.getValue().values()) {
        ShipmentItemEntity item = new ShipmentItemEntity();
        item.setShipment(shipment);
        item.setOrderItem(orderItems.get(a.variantId()));
        item.setInventory(a.inventory());
        item.setQuantity(a.quantity());
        shipmentItems.add(item);
      }
      shipmentItemRepository.saveAll(shipmentItems);
      trackingEventRepository.save(trackingEvent(shipment, "Shipment created and stock reserved."));
    }
    cart.getItems().clear();
    paymentService.createPending(order, "COD", "checkout-" + order.getOrderNumber());
    notificationService.queueOrderCreated(order);
    return CheckoutResponseBuilder.build(order, address, shipmentPlan(allocation), checkoutItems);
  }

  private Map<WarehouseEntity, Map<UUID, Allocation>> allocate(
      List<WarehouseEntity> ranked, Map<UUID, Integer> quantities, boolean reserveStock) {
    // First pass preserves the single nearest warehouse preference for the entire order.
    for (WarehouseEntity warehouse : ranked) {
      Map<UUID, InventoryEntity> stock = stock(warehouse, quantities.keySet(), reserveStock);
      if (quantities.entrySet().stream()
          .allMatch(
              q ->
                  stock.containsKey(q.getKey())
                      && stock.get(q.getKey()).getAvailableQuantity() >= q.getValue())) {
        Map<UUID, Allocation> one = new LinkedHashMap<>();
        quantities.forEach(
            (variant, quantity) -> {
              InventoryEntity i = stock.get(variant);
              if (reserveStock) {
                i.reserve(quantity);
              }
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
            stock(warehouse, List.of(request.getKey()), reserveStock).get(request.getKey());
        if (inventory == null || inventory.getAvailableQuantity() == 0) continue;
        int take = Math.min(remaining, inventory.getAvailableQuantity());
        if (reserveStock) {
          inventory.reserve(take);
        }
        result
            .computeIfAbsent(warehouse, ignored -> new LinkedHashMap<>())
            .put(request.getKey(), new Allocation(request.getKey(), take, inventory));
        remaining -= take;
        if (remaining == 0) break;
      }
      if (remaining > 0)
        throw new BusinessException(
            "INSUFFICIENT_INVENTORY",
            "Sản phẩm trong giỏ không đủ hàng tại khu vực giao đã chọn. Vui lòng chọn địa chỉ khác hoặc quay lại sau.",
            HttpStatus.CONFLICT);
    }
    return result;
  }

  private UserAddressEntity address(UUID userId, UUID addressId) {
    return addressRepository
        .findById(addressId)
        .filter(a -> !a.isDeleted() && a.getUser().getId().equals(userId))
        .orElseThrow(
            () ->
                new BusinessException(
                    "USER_ADDRESS_NOT_FOUND", "Address not found", HttpStatus.NOT_FOUND));
  }

  private CartEntity cart(UUID userId) {
    CartEntity cart =
        cartRepository
            .findCurrentByUserId(userId)
            .orElseThrow(
                () ->
                    new BusinessException(
                        "CART_NOT_FOUND", "CartEntity not found", HttpStatus.NOT_FOUND));
    if (cart.getItems().isEmpty()) {
      throw new BusinessException("CART_EMPTY", "CartEntity is empty", HttpStatus.BAD_REQUEST);
    }
    return cart;
  }

  private Map<UUID, Integer> quantities(CartEntity cart) {
    return cart.getItems().stream()
        .collect(
            Collectors.toMap(
                i -> i.getVariant().getId(),
                i -> i.getQuantity(),
                Integer::sum,
                LinkedHashMap::new));
  }

  private Map<UUID, Integer> availableQuantities(
      List<WarehouseEntity> ranked, Collection<UUID> variantIds) {
    Map<UUID, Integer> available = new LinkedHashMap<>();

    for (WarehouseEntity warehouse : ranked) {
      stock(warehouse, variantIds, false)
          .forEach(
              (variantId, inventory) ->
                  available.merge(variantId, inventory.getAvailableQuantity(), Integer::sum));
    }

    return available;
  }

  private List<WarehouseEntity> rankedWarehouses(UserAddressEntity address) {
    ShippingZoneEntity shippingZone = shippingZone(address);
    List<WarehouseShippingZoneEntity> mappings =
        warehouseShippingZoneRepository.findActiveByShippingZoneId(shippingZone.getId());

    List<WarehouseEntity> ranked =
        mappings.stream()
            .sorted(
                Comparator.comparingInt(WarehouseShippingZoneEntity::getPriority)
                    .thenComparingDouble(
                        mapping -> {
                          WarehouseEntity w = mapping.getWarehouse();
                          return Haversine.kilometers(
                              address.getLatitude(),
                              address.getLongitude(),
                              w.getLatitude(),
                              w.getLongitude());
                        })
                    .thenComparing(mapping -> mapping.getWarehouse().getId()))
            .map(WarehouseShippingZoneEntity::getWarehouse)
            .toList();

    if (ranked.isEmpty()) {
      throw new BusinessException(
          "DELIVERY_ZONE_NOT_SUPPORTED",
          "No active warehouse can serve " + shippingZone.getName(),
          HttpStatus.CONFLICT);
    }

    return ranked;
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

  private Map<UUID, InventoryEntity> stock(
      WarehouseEntity warehouse, Collection<UUID> variantIds, boolean locked) {
    List<InventoryEntity> inventories =
        locked
            ? inventoryRepository.lockByWarehouseAndVariants(warehouse.getId(), variantIds)
            : inventoryRepository.findByWarehouseAndVariants(warehouse.getId(), variantIds);
    return inventories.stream().collect(Collectors.toMap(i -> i.getVariant().getId(), i -> i));
  }

  private String trackingNumber(WarehouseEntity warehouse) {
    return "VNPOST-" + warehouse.getCode() + "-" + UUID.randomUUID().toString().substring(0, 8);
  }

  private TrackingEventEntity trackingEvent(ShipmentEntity shipment, String note) {
    TrackingEventEntity event = new TrackingEventEntity();
    event.setShipment(shipment);
    event.setStatus(shipment.getStatus());
    event.setNote(note);
    event.setOccurredAt(java.time.Instant.now());
    return event;
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
