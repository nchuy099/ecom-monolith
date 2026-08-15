package com.ecomlab.ecommerce.controller;

import com.ecomlab.ecommerce.dto.response.ShipmentResponse;
import com.ecomlab.ecommerce.service.CustomerShipmentService;
import java.util.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/shipments")
@RequiredArgsConstructor
public class ShipmentController {
  private final CustomerShipmentService customerShipmentService;

  @GetMapping
  public ResponseEntity<List<ShipmentResponse>> getMyShipments(Authentication a) {
    return ResponseEntity.ok(customerShipmentService.getMyShipments(UUID.fromString(a.getName())));
  }
}
