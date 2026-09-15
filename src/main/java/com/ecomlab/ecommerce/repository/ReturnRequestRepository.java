package com.ecomlab.ecommerce.repository;

import com.ecomlab.ecommerce.entity.ReturnRequestEntity;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ReturnRequestRepository extends JpaRepository<ReturnRequestEntity, UUID> {
  @Query(
      """
      select distinct r from ReturnRequestEntity r join fetch r.order o
      left join fetch r.items ri join fetch ri.orderItem
      where o.user.id = :userId and r.isDeleted = false
      order by r.createdAt desc
      """)
  List<ReturnRequestEntity> findAllByUserId(@Param("userId") UUID userId);

  @Query(
      """
      select distinct r from ReturnRequestEntity r join fetch r.order o
      left join fetch r.items ri join fetch ri.orderItem
      where r.id = :returnId and o.user.id = :userId and r.isDeleted = false
      """)
  Optional<ReturnRequestEntity> findOwnedByIdWithItems(
      @Param("returnId") UUID returnId, @Param("userId") UUID userId);

  @Query(
      """
      select distinct r from ReturnRequestEntity r join fetch r.order o
      left join fetch r.items ri join fetch ri.orderItem
      where r.isDeleted = false order by r.updatedAt desc
      """)
  List<ReturnRequestEntity> findAllActiveWithItems();

  @Query(
      """
      select distinct r from ReturnRequestEntity r join fetch r.order o
      left join fetch r.items ri join fetch ri.orderItem
      where r.id = :returnId and r.isDeleted = false
      """)
  Optional<ReturnRequestEntity> findByIdWithItems(@Param("returnId") UUID returnId);

  @Query(
      """
      select ri from ReturnItemEntity ri join fetch ri.returnRequest r
      where ri.orderItem.id = :orderItemId and r.status <> com.ecomlab.ecommerce.common.enums.ReturnStatus.REJECTED
        and r.isDeleted = false
      """)
  List<com.ecomlab.ecommerce.entity.ReturnItemEntity> findNonRejectedItemsByOrderItemId(
      @Param("orderItemId") UUID orderItemId);

  @Query(
      """
      select distinct r from ReturnRequestEntity r join fetch r.items ri join fetch ri.orderItem
      where r.order.id = :orderId and r.isDeleted = false
      """)
  List<ReturnRequestEntity> findAllByOrderIdWithItems(@Param("orderId") UUID orderId);
}
