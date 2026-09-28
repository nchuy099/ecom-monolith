package com.ecomlab.ecommerce.repository;

import com.ecomlab.ecommerce.common.enums.NotificationStatus;
import com.ecomlab.ecommerce.entity.*;
import java.time.Instant;
import java.util.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
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

  @Modifying
  @Query(
      value = """
      update NotificationEntity n
      set n.readAt = :readAt
      where n.user.id = :userId
        and n.readAt is null
        and n.isDeleted = false
      """)
  int markAllRead(@Param("userId") UUID userId, @Param("readAt") Instant readAt);

  @Query(
      value = """
      select n.*
      from notifications n
      where n.status = :status
        and n.next_attempt_at <= :now
        and n.is_deleted = false
      order by n.next_attempt_at asc, n.created_at asc
      limit :batchSize
      for update skip locked
      """,
      nativeQuery = true)
  List<NotificationEntity> lockDueNotifications(
      @Param("status") String status,
      @Param("now") Instant now,
      @Param("batchSize") int batchSize);

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
