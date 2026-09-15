package com.ecomlab.ecommerce.service;

import com.ecomlab.ecommerce.dto.request.CreateReturnRequest;
import com.ecomlab.ecommerce.dto.request.ReceiveReturnShipmentRequest;
import com.ecomlab.ecommerce.dto.request.ReturnDecisionRequest;
import com.ecomlab.ecommerce.dto.response.ReturnResponse;
import java.util.List;
import java.util.UUID;

public interface ReturnService {
  List<ReturnResponse> getMyReturns(UUID userId);

  ReturnResponse create(UUID userId, CreateReturnRequest request);

  ReturnResponse getMyReturn(UUID userId, UUID returnId);

  List<ReturnResponse> adminReturns();

  ReturnResponse adminReturn(UUID returnId);

  ReturnResponse approve(UUID returnId, ReturnDecisionRequest request);

  ReturnResponse reject(UUID returnId, ReturnDecisionRequest request);

  ReturnResponse receive(UUID shipmentId, ReceiveReturnShipmentRequest request);

  ReturnResponse refund(UUID returnId);
}
