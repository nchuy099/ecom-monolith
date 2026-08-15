package com.ecomlab.ecommerce.service.impl;

import com.ecomlab.ecommerce.common.enums.OrderStatus;
import com.ecomlab.ecommerce.common.enums.ReturnStatus;
import com.ecomlab.ecommerce.dto.request.CreateReturnRequest;
import com.ecomlab.ecommerce.dto.response.ReturnResponse;
import com.ecomlab.ecommerce.entity.OrderEntity;
import com.ecomlab.ecommerce.entity.ReturnRequestEntity;
import com.ecomlab.ecommerce.exception.BusinessException;
import com.ecomlab.ecommerce.repository.OrderRepository;
import com.ecomlab.ecommerce.repository.ReturnRequestRepository;
import com.ecomlab.ecommerce.service.ReturnService;
import com.ecomlab.ecommerce.service.builder.ReturnResponseBuilder;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ReturnServiceImpl implements ReturnService {
  private final OrderRepository orderRepository;
  private final ReturnRequestRepository returnRequestRepository;

  @Transactional(readOnly = true)
  public List<ReturnResponse> getMyReturns(UUID userId) {
    return returnRequestRepository.findAllByUserId(userId).stream()
        .map(ReturnResponseBuilder::build)
        .toList();
  }

  @Transactional
  public ReturnResponse create(UUID userId, CreateReturnRequest request) {
    OrderEntity order =
        orderRepository
            .findOwnedByIdWithItems(request.getOrderId(), userId)
            .orElseThrow(
                () ->
                    new BusinessException(
                        "ORDER_NOT_FOUND", "Order not found", HttpStatus.NOT_FOUND));
    if (order.getStatus() != OrderStatus.COMPLETED)
      throw new BusinessException(
          "RETURN_NOT_ALLOWED",
          "Only completed orderRepository can be returned",
          HttpStatus.CONFLICT);
    ReturnRequestEntity value = new ReturnRequestEntity();
    value.setOrder(order);
    value.setReason(request.getReason());
    value.setStatus(ReturnStatus.REQUESTED);
    return ReturnResponseBuilder.build(returnRequestRepository.save(value));
  }
}
