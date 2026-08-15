package com.ecomlab.ecommerce.repository;

import com.ecomlab.ecommerce.common.enums.OrderStatus;
import com.ecomlab.ecommerce.dto.response.OrderListCursor;
import com.ecomlab.ecommerce.entity.OrderEntity;
import com.ecomlab.ecommerce.specification.OrderSpecifications;
import jakarta.persistence.EntityManager;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Repository;

@Repository
public class OrderQueryRepositoryImpl implements OrderQueryRepository {
  private final EntityManager entityManager;

  public OrderQueryRepositoryImpl(EntityManager entityManager) {
    this.entityManager = entityManager;
  }

  @Override
  public List<OrderEntity> findMyOrdersAfterCursor(
      UUID userId, OrderStatus status, OrderListCursor cursor, int limit) {
    List<UUID> orderIds = findOrderIdsAfterCursor(userId, status, cursor, limit);
    if (orderIds.isEmpty()) {
      return List.of();
    }

    Map<UUID, Integer> positions = positions(orderIds);

    return entityManager
        .createQuery(
            """
            select distinct o
            from OrderEntity o
            left join fetch o.items i
            left join fetch i.variant
            where o.id in :orderIds
            """,
            OrderEntity.class)
        .setParameter("orderIds", orderIds)
        .getResultList()
        .stream()
        .sorted(Comparator.comparing(order -> positions.get(order.getId())))
        .toList();
  }

  private List<UUID> findOrderIdsAfterCursor(
      UUID userId, OrderStatus status, OrderListCursor cursor, int limit) {
    CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
    CriteriaQuery<UUID> query = criteriaBuilder.createQuery(UUID.class);
    Root<OrderEntity> orderRoot = query.from(OrderEntity.class);
    Specification<OrderEntity> specification =
        Specification.allOf(
            OrderSpecifications.visibleToUser(userId), OrderSpecifications.hasStatus(status));
    Predicate filterPredicate = specification.toPredicate(orderRoot, query, criteriaBuilder);
    Predicate cursorPredicate = afterCursor(orderRoot, criteriaBuilder, cursor);

    query.select(orderRoot.get("id"));
    query.where(criteriaBuilder.and(filterPredicate, cursorPredicate));
    query.orderBy(
        criteriaBuilder.desc(orderRoot.get("createdAt")),
        criteriaBuilder.desc(orderRoot.get("id")));

    return entityManager.createQuery(query).setMaxResults(limit).getResultList();
  }

  private Predicate afterCursor(
      Root<OrderEntity> orderRoot, CriteriaBuilder criteriaBuilder, OrderListCursor cursor) {
    if (cursor == null || cursor.createdAt() == null || cursor.id() == null) {
      return criteriaBuilder.conjunction();
    }

    return criteriaBuilder.or(
        criteriaBuilder.lessThan(orderRoot.get("createdAt"), cursor.createdAt()),
        criteriaBuilder.and(
            criteriaBuilder.equal(orderRoot.get("createdAt"), cursor.createdAt()),
            criteriaBuilder.lessThan(orderRoot.get("id"), cursor.id())));
  }

  private Map<UUID, Integer> positions(List<UUID> orderIds) {
    Map<UUID, Integer> positions = new HashMap<>();

    for (int index = 0; index < orderIds.size(); index++) {
      positions.put(orderIds.get(index), index);
    }

    return positions;
  }
}
