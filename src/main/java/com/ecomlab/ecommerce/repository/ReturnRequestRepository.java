package com.ecomlab.ecommerce.repository;

import com.ecomlab.ecommerce.entity.ReturnRequestEntity;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ReturnRequestRepository extends JpaRepository<ReturnRequestEntity, UUID> {
  @Query(
      """
      select r from ReturnRequestEntity r join fetch r.order o
      where o.user.id = :userId and r.isDeleted = false
      order by r.createdAt desc
      """)
  List<ReturnRequestEntity> findAllByUserId(@Param("userId") UUID userId);
}
