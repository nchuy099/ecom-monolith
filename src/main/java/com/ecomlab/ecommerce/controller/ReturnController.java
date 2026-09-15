package com.ecomlab.ecommerce.controller;

import com.ecomlab.ecommerce.dto.request.CreateReturnRequest;
import com.ecomlab.ecommerce.dto.response.ReturnResponse;
import com.ecomlab.ecommerce.service.ReturnService;
import jakarta.validation.Valid;
import java.util.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/returns")
@PreAuthorize("hasRole('CUSTOMER')")
@RequiredArgsConstructor
public class ReturnController {
  private final ReturnService returnService;

  @GetMapping
  public ResponseEntity<List<ReturnResponse>> getMyReturns(Authentication a) {
    return ResponseEntity.ok(returnService.getMyReturns(UUID.fromString(a.getName())));
  }

  @PostMapping
  public ResponseEntity<ReturnResponse> create(
      Authentication a, @Valid @RequestBody CreateReturnRequest r) {
    return ResponseEntity.status(HttpStatus.CREATED)
        .body(returnService.create(UUID.fromString(a.getName()), r));
  }

  @GetMapping("/{returnId}")
  public ResponseEntity<ReturnResponse> get(Authentication a, @PathVariable UUID returnId) {
    return ResponseEntity.ok(returnService.getMyReturn(UUID.fromString(a.getName()), returnId));
  }
}
