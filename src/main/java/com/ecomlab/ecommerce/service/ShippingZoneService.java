package com.ecomlab.ecommerce.service;

import com.ecomlab.ecommerce.dto.request.ShippingZoneRequest;
import com.ecomlab.ecommerce.dto.response.ShippingZoneResponse;
import java.util.List;
import java.util.UUID;

public interface ShippingZoneService {
  List<ShippingZoneResponse> shippingZones();

  ShippingZoneResponse create(ShippingZoneRequest request);

  ShippingZoneResponse update(UUID id, ShippingZoneRequest request);

  void delete(UUID id);
}
