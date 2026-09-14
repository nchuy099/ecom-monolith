package com.ecomlab.ecommerce.specification;

import com.ecomlab.ecommerce.common.enums.OrderStatus;
import com.ecomlab.ecommerce.entity.OrderEntity;
import java.util.UUID;
import org.springframework.data.jpa.domain.Specification;

public final class OrderSpecifications {
  private OrderSpecifications() {}

  public static Specification<OrderEntity> visible() {
    return (root, query, criteriaBuilder) -> criteriaBuilder.isFalse(root.get("isDeleted"));
  }

  public static Specification<OrderEntity> visibleToUser(UUID userId) {
    return (root, query, criteriaBuilder) ->
        criteriaBuilder.and(
            criteriaBuilder.isFalse(root.get("isDeleted")),
            criteriaBuilder.equal(root.get("user").get("id"), userId));
  }

  public static Specification<OrderEntity> hasStatus(OrderStatus status) {
    return (root, query, criteriaBuilder) ->
        status == null
            ? criteriaBuilder.conjunction()
            : criteriaBuilder.equal(root.get("status"), status);
  }
}
