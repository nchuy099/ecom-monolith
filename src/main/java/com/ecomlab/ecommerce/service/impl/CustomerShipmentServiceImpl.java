package com.ecomlab.ecommerce.service.impl;

import com.ecomlab.ecommerce.dto.response.ShipmentResponse;
import com.ecomlab.ecommerce.repository.ShipmentRepository;
import com.ecomlab.ecommerce.service.CustomerShipmentService;
import com.ecomlab.ecommerce.service.builder.ShipmentResponseBuilder;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CustomerShipmentServiceImpl implements CustomerShipmentService {
  private final ShipmentRepository shipmentRepository;

  @Transactional(readOnly = true)
  public List<ShipmentResponse> getMyShipments(UUID userId) {
    return shipmentRepository.findAllByCustomerId(userId).stream()
        .map(ShipmentResponseBuilder::build)
        .toList();
  }
}
