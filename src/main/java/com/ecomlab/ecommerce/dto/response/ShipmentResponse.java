package com.ecomlab.ecommerce.dto.response;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ShipmentResponse {
  private UUID id;
  private UUID orderId;
  private String orderNumber;
  private UUID warehouseId;
  private String warehouseName;
  private UUID shipperId;
  private String shipperName;
  private String status;
  private String type;
  private UUID returnId;
  private Instant warehouseReceivedAt;
  private List<ShipmentItemResponse> items;
  private String trackingNumber;
  private String recipientName;
  private String recipientPhone;
  private String deliveryAddress;
  private BigDecimal deliveryLatitude;
  private BigDecimal deliveryLongitude;
  private BigDecimal warehouseLatitude;
  private BigDecimal warehouseLongitude;
  private double distanceKm;
  private Instant updatedAt;
  private List<TrackingEventResponse> timeline;
}
