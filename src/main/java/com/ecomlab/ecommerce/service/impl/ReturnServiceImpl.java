package com.ecomlab.ecommerce.service.impl;

import com.ecomlab.ecommerce.common.enums.*;
import com.ecomlab.ecommerce.dto.request.*;
import com.ecomlab.ecommerce.dto.response.ReturnResponse;
import com.ecomlab.ecommerce.entity.*;
import com.ecomlab.ecommerce.exception.BusinessException;
import com.ecomlab.ecommerce.repository.*;
import com.ecomlab.ecommerce.service.ReturnService;
import com.ecomlab.ecommerce.service.builder.ReturnResponseBuilder;
import java.math.BigDecimal;
import java.time.Duration;
import java.time.Instant;
import java.util.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ReturnServiceImpl implements ReturnService {
  private static final Duration RETURN_WINDOW = Duration.ofDays(7);

  private final OrderRepository orderRepository;
  private final ReturnRequestRepository returnRequestRepository;
  private final ShipmentRepository shipmentRepository;
  private final ShipmentItemRepository shipmentItemRepository;
  private final PaymentRepository paymentRepository;
  private final PaymentRefundRepository paymentRefundRepository;

  @Override
  @Transactional(readOnly = true)
  public List<ReturnResponse> getMyReturns(UUID userId) {
    return returnRequestRepository.findAllByUserId(userId).stream().map(this::response).toList();
  }

  @Override
  @Transactional(readOnly = true)
  public ReturnResponse getMyReturn(UUID userId, UUID returnId) {
    return response(owned(returnId, userId));
  }

  @Override
  @Transactional
  public ReturnResponse create(UUID userId, CreateReturnRequest request) {
    OrderEntity order =
        orderRepository
            .findOwnedByIdWithItems(request.getOrderId(), userId)
            .orElseThrow(() -> error("ORDER_NOT_FOUND", "Order not found", HttpStatus.NOT_FOUND));
    ensureEligible(order);
    if (request.getItems() == null || request.getItems().isEmpty()) {
      throw error("RETURN_ITEMS_REQUIRED", "At least one return item is required", HttpStatus.BAD_REQUEST);
    }

    Map<UUID, OrderItemEntity> orderItems = new HashMap<>();
    order.getItems().forEach(item -> orderItems.put(item.getId(), item));
    Set<UUID> seen = new HashSet<>();
    ReturnRequestEntity value = new ReturnRequestEntity();
    value.setOrder(order);
    value.setReason(request.getReason().trim());
    value.setStatus(ReturnStatus.REQUESTED);
    order.setStatus(OrderStatus.RETURN_REQUESTED);

    for (ReturnItemRequest requested : request.getItems()) {
      if (!seen.add(requested.getOrderItemId())) {
        throw error("DUPLICATE_RETURN_ITEM", "Each order item can only appear once", HttpStatus.BAD_REQUEST);
      }
      OrderItemEntity orderItem = orderItems.get(requested.getOrderItemId());
      if (orderItem == null) {
        throw error("RETURN_ITEM_NOT_FOUND", "Return item does not belong to this order", HttpStatus.BAD_REQUEST);
      }
      int committed =
          returnRequestRepository.findNonRejectedItemsByOrderItemId(orderItem.getId()).stream()
              .mapToInt(ReturnItemEntity::getQuantity)
              .sum();
      if (requested.getQuantity() > orderItem.getQuantity() - committed) {
        throw error(
            "RETURN_QUANTITY_INVALID",
            "Requested quantity exceeds delivered quantity",
            HttpStatus.CONFLICT);
      }
      ReturnItemEntity item = new ReturnItemEntity();
      item.setReturnRequest(value);
      item.setOrderItem(orderItem);
      item.setQuantity(requested.getQuantity());
      item.setReceivedQuantity(0);
      item.setRestockedQuantity(0);
      value.getItems().add(item);
    }
    return response(returnRequestRepository.save(value));
  }

  @Override
  @Transactional(readOnly = true)
  public List<ReturnResponse> adminReturns() {
    return returnRequestRepository.findAllActiveWithItems().stream().map(this::response).toList();
  }

  @Override
  @Transactional(readOnly = true)
  public ReturnResponse adminReturn(UUID returnId) {
    return response(value(returnId));
  }

  @Override
  @Transactional
  public ReturnResponse approve(UUID returnId, ReturnDecisionRequest request) {
    ReturnRequestEntity value = value(returnId);
    transition(value, ReturnStatus.REQUESTED, ReturnStatus.APPROVED);
    value.setDecisionNote(trimToNull(request.getNote()));
    createReturnShipments(value);
    return response(value);
  }

  @Override
  @Transactional
  public ReturnResponse reject(UUID returnId, ReturnDecisionRequest request) {
    ReturnRequestEntity value = value(returnId);
    if (request.getNote() == null || request.getNote().isBlank()) {
      throw error(
          "RETURN_REJECTION_NOTE_REQUIRED", "A rejection note is required", HttpStatus.BAD_REQUEST);
    }
    transition(value, ReturnStatus.REQUESTED, ReturnStatus.REJECTED);
    value.setDecisionNote(request.getNote().trim());
    restoreOrderStatusAfterRejection(value.getOrder());
    return response(value);
  }

  @Override
  @Transactional
  public ReturnResponse receive(UUID shipmentId, ReceiveReturnShipmentRequest request) {
    ShipmentEntity shipment =
        shipmentRepository
            .findByIdWithReturnItems(shipmentId)
            .orElseThrow(
                () ->
                    error(
                        "RETURN_SHIPMENT_NOT_FOUND", "Return shipment not found", HttpStatus.NOT_FOUND));
    if (shipment.getType() != ShipmentType.RETURN || shipment.getStatus() != ShipmentStatus.DELIVERED) {
      throw error(
          "RETURN_SHIPMENT_NOT_RECEIVABLE",
          "Return shipment has not reached the warehouse",
          HttpStatus.CONFLICT);
    }
    if (shipment.getWarehouseReceivedAt() != null) {
      throw error(
          "RETURN_SHIPMENT_ALREADY_RECEIVED",
          "Return shipment is already received",
          HttpStatus.CONFLICT);
    }
    Map<UUID, ShipmentItemEntity> shipmentItems = new HashMap<>();
    shipment.getItems().forEach(item -> shipmentItems.put(item.getId(), item));
    if (shipmentItems.size() != request.getItems().size()) {
      throw error(
          "RETURN_RECEIPT_ITEMS_INVALID",
          "Provide every return shipment item exactly once",
          HttpStatus.BAD_REQUEST);
    }
    Set<UUID> seen = new HashSet<>();
    for (ReturnShipmentReceiptItemRequest receipt : request.getItems()) {
      ShipmentItemEntity item = shipmentItems.get(receipt.getShipmentItemId());
      if (item == null
          || !seen.add(receipt.getShipmentItemId())
          || item.getReturnItem() == null
          || receipt.getReceivedQuantity() > item.getQuantity()
          || receipt.getRestockedQuantity() > receipt.getReceivedQuantity()) {
        throw error(
            "RETURN_RECEIPT_ITEMS_INVALID", "Invalid return receipt quantities", HttpStatus.BAD_REQUEST);
      }
      ReturnItemEntity returnItem = item.getReturnItem();
      returnItem.setReceivedQuantity(returnItem.getReceivedQuantity() + receipt.getReceivedQuantity());
      returnItem.setRestockedQuantity(returnItem.getRestockedQuantity() + receipt.getRestockedQuantity());
      if (receipt.getRestockedQuantity() > 0) item.getInventory().restock(receipt.getRestockedQuantity());
    }
    shipment.setWarehouseReceivedAt(Instant.now());
    ReturnRequestEntity value = shipment.getReturnRequest();
    boolean allReceived =
        shipmentRepository.findReturnShipmentsByReturnId(value.getId()).stream()
            .allMatch(candidate -> candidate.getWarehouseReceivedAt() != null);
    if (allReceived) value.setStatus(ReturnStatus.RESTOCKED);
    return response(value);
  }

  @Override
  @Transactional
  public ReturnResponse refund(UUID returnId) {
    ReturnRequestEntity value = value(returnId);
    if (value.getStatus() != ReturnStatus.RESTOCKED) {
      throw error(
          "RETURN_NOT_READY_FOR_REFUND",
          "Return must be received by the warehouse first",
          HttpStatus.CONFLICT);
    }
    if (paymentRefundRepository.findByReturnRequestId(returnId).isPresent()) {
      throw error("RETURN_ALREADY_REFUNDED", "Return has already been refunded", HttpStatus.CONFLICT);
    }
    BigDecimal amount = refundableAmount(value);
    if (amount.signum() <= 0) {
      throw error("RETURN_NOT_REFUNDABLE", "No received items are eligible for a refund", HttpStatus.CONFLICT);
    }
    PaymentEntity payment =
        paymentRepository
            .findSucceededByOrderId(value.getOrder().getId())
            .stream()
            .findFirst()
            .orElseThrow(
                () ->
                    error(
                        "PAYMENT_NOT_REFUNDABLE",
                        "No successful payment is available",
                        HttpStatus.CONFLICT));
    BigDecimal totalRefunded = paymentRefundRepository.sumActiveByPaymentId(payment.getId());
    if (totalRefunded.add(amount).compareTo(payment.getAmount()) > 0) {
      throw error("REFUND_AMOUNT_INVALID", "Refund exceeds paid amount", HttpStatus.CONFLICT);
    }
    PaymentRefundEntity refund = new PaymentRefundEntity();
    refund.setPayment(payment);
    refund.setReturnRequest(value);
    refund.setAmount(amount);
    refund.setProviderReference(
        "REF-" + returnId.toString().substring(0, 8).toUpperCase() + "-" + Instant.now().toEpochMilli());
    refund.setRefundedAt(Instant.now());
    paymentRefundRepository.save(refund);
    if (totalRefunded.add(amount).compareTo(payment.getAmount()) == 0) {
      payment.setStatus(PaymentStatus.REFUNDED);
    }
    value.setStatus(ReturnStatus.REFUNDED);
    updateOrderStatusAfterRefund(value.getOrder());
    return response(value);
  }

  private void createReturnShipments(ReturnRequestEntity value) {
    Map<UUID, Integer> remaining = new HashMap<>();
    Map<UUID, ReturnItemEntity> returnItems = new HashMap<>();
    value.getItems().forEach(
        item -> {
          remaining.put(item.getOrderItem().getId(), item.getQuantity());
          returnItems.put(item.getOrderItem().getId(), item);
        });
    Map<UUID, List<ShipmentItemEntity>> byWarehouse = new LinkedHashMap<>();
    for (ShipmentEntity outbound : shipmentRepository.findActiveByOrderIdWithItems(value.getOrder().getId())) {
      if (outbound.getStatus() != ShipmentStatus.DELIVERED) continue;
      for (ShipmentItemEntity source : outbound.getItems()) {
        int needed = remaining.getOrDefault(source.getOrderItem().getId(), 0);
        if (needed == 0) continue;
        int quantity = Math.min(needed, source.getQuantity());
        ShipmentItemEntity item = new ShipmentItemEntity();
        item.setOrderItem(source.getOrderItem());
        item.setInventory(source.getInventory());
        item.setReturnItem(returnItems.get(source.getOrderItem().getId()));
        item.setQuantity(quantity);
        byWarehouse.computeIfAbsent(outbound.getWarehouse().getId(), ignored -> new ArrayList<>()).add(item);
        remaining.put(source.getOrderItem().getId(), needed - quantity);
      }
    }
    if (remaining.values().stream().anyMatch(quantity -> quantity > 0)) {
      throw error(
          "RETURN_ALLOCATION_FAILED",
          "Requested items cannot be mapped to delivered shipments",
          HttpStatus.CONFLICT);
    }
    for (List<ShipmentItemEntity> items : byWarehouse.values()) {
      InventoryEntity inventory = items.getFirst().getInventory();
      ShipmentEntity shipment = new ShipmentEntity();
      shipment.setOrder(value.getOrder());
      shipment.setReturnRequest(value);
      shipment.setWarehouse(inventory.getWarehouse());
      shipment.setTrackingNumber(
          "RETURN-" + inventory.getWarehouse().getCode() + "-" + UUID.randomUUID().toString().substring(0, 8));
      shipment.setStatus(ShipmentStatus.PENDING_PACKING);
      shipment.setType(ShipmentType.RETURN);
      shipmentRepository.save(shipment);
      items.forEach(item -> item.setShipment(shipment));
      shipmentItemRepository.saveAll(items);
    }
  }

  private void ensureEligible(OrderEntity order) {
    if ((order.getStatus() != OrderStatus.COMPLETED
            && order.getStatus() != OrderStatus.PARTIALLY_RETURNED)
        || order.getCompletedAt() == null
        || order.getCompletedAt().plus(RETURN_WINDOW).isBefore(Instant.now())) {
      throw error(
          "RETURN_NOT_ALLOWED",
          "Only orders delivered in the last 7 days can be returned",
          HttpStatus.CONFLICT);
    }
  }

  private ReturnRequestEntity value(UUID returnId) {
    return returnRequestRepository
        .findByIdWithItems(returnId)
        .orElseThrow(() -> error("RETURN_NOT_FOUND", "Return request not found", HttpStatus.NOT_FOUND));
  }

  private ReturnRequestEntity owned(UUID returnId, UUID userId) {
    return returnRequestRepository
        .findOwnedByIdWithItems(returnId, userId)
        .orElseThrow(() -> error("RETURN_NOT_FOUND", "Return request not found", HttpStatus.NOT_FOUND));
  }

  private ReturnResponse response(ReturnRequestEntity value) {
    return ReturnResponseBuilder.build(
        value, shipmentRepository.findReturnShipmentsByReturnId(value.getId()));
  }

  private BigDecimal refundableAmount(ReturnRequestEntity value) {
    return value.getItems().stream()
        .map(
            item ->
                item.getOrderItem()
                    .getUnitPrice()
                    .multiply(BigDecimal.valueOf(item.getReceivedQuantity())))
        .reduce(BigDecimal.ZERO, BigDecimal::add);
  }

  private void transition(ReturnRequestEntity value, ReturnStatus from, ReturnStatus to) {
    if (value.getStatus() != from) {
      throw error("INVALID_RETURN_TRANSITION", "Invalid return status transition", HttpStatus.CONFLICT);
    }
    value.setStatus(to);
  }

  private void restoreOrderStatusAfterRejection(OrderEntity order) {
    boolean hasActiveReturn =
        returnRequestRepository.findAllByOrderIdWithItems(order.getId()).stream()
            .anyMatch(value -> value.getStatus() != ReturnStatus.REJECTED && value.getStatus() != ReturnStatus.REFUNDED);
    if (!hasActiveReturn) {
      updateOrderStatusAfterRefund(order);
    }
  }

  private void updateOrderStatusAfterRefund(OrderEntity order) {
    Map<UUID, Integer> refundedQuantities = new HashMap<>();
    returnRequestRepository.findAllByOrderIdWithItems(order.getId()).stream()
        .filter(value -> value.getStatus() == ReturnStatus.REFUNDED)
        .flatMap(value -> value.getItems().stream())
        .forEach(
            item ->
                refundedQuantities.merge(
                    item.getOrderItem().getId(), item.getReceivedQuantity(), Integer::sum));
    boolean anyReturned = refundedQuantities.values().stream().anyMatch(quantity -> quantity > 0);
    boolean allReturned =
        anyReturned
            && order.getItems().stream()
                .allMatch(item -> refundedQuantities.getOrDefault(item.getId(), 0) >= item.getQuantity());
    order.setStatus(allReturned ? OrderStatus.RETURNED : anyReturned ? OrderStatus.PARTIALLY_RETURNED : OrderStatus.COMPLETED);
  }

  private BusinessException error(String code, String message, HttpStatus status) {
    return new BusinessException(code, message, status);
  }

  private String trimToNull(String value) {
    return value == null || value.isBlank() ? null : value.trim();
  }
}
