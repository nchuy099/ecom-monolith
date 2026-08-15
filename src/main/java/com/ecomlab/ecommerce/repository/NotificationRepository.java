package com.ecomlab.ecommerce.repository;

import com.ecomlab.ecommerce.common.enums.NotificationStatus;
import com.ecomlab.ecommerce.entity.*;
import jakarta.persistence.LockModeType;
import java.time.Instant;
import java.util.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface NotificationRepository extends JpaRepository<NotificationEntity, UUID> {
  List<NotificationEntity> findByUserIdAndIsDeletedFalseOrderByCreatedAtDesc(UUID userId);

  Optional<NotificationEntity> findByIdAndUserIdAndIsDeletedFalse(UUID id, UUID userId);

  Page<NotificationEntity> findByIsDeletedFalse(Pageable pageable);

  Page<NotificationEntity> findByStatusAndIsDeletedFalse(
      NotificationStatus status, Pageable pageable);

  @Lock(LockModeType.PESSIMISTIC_WRITE)
  @Query(
      """
      select n
      from NotificationEntity n
      left join fetch n.user
      left join fetch n.order
      where n.status = :status
        and n.nextAttemptAt <= :now
        and n.isDeleted = false
      order by n.nextAttemptAt asc, n.createdAt asc
      """)
  List<NotificationEntity> lockDueNotifications(
      @Param("status") NotificationStatus status, @Param("now") Instant now, Pageable pageable);

  @Modifying
  @Query(
      """
      update NotificationEntity n
      set n.status = com.ecomlab.ecommerce.common.enums.NotificationStatus.PENDING,
          n.claimedAt = null
      where n.status = com.ecomlab.ecommerce.common.enums.NotificationStatus.PROCESSING
        and n.claimedAt < :expiredBefore
        and n.isDeleted = false
      """)
  int requeueExpiredClaims(@Param("expiredBefore") Instant expiredBefore);
}
