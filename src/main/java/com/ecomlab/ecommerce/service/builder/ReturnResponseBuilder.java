package com.ecomlab.ecommerce.service.builder;

import com.ecomlab.ecommerce.dto.response.ReturnResponse;
import com.ecomlab.ecommerce.entity.ReturnRequestEntity;

public final class ReturnResponseBuilder {
  private ReturnResponseBuilder() {}

  public static ReturnResponse build(ReturnRequestEntity value) {
    return ReturnResponse.builder()
        .id(value.getId())
        .orderId(value.getOrder().getId())
        .status(value.getStatus().name())
        .reason(value.getReason())
        .build();
  }
}
