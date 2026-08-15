package com.ecomlab.ecommerce.service;

import com.ecomlab.ecommerce.dto.request.CreateReturnRequest;
import com.ecomlab.ecommerce.dto.response.ReturnResponse;
import java.util.List;
import java.util.UUID;

public interface ReturnService {
  List<ReturnResponse> getMyReturns(UUID userId);

  ReturnResponse create(UUID userId, CreateReturnRequest request);
}
