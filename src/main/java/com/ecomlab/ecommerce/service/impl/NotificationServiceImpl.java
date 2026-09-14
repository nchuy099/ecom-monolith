package com.ecomlab.ecommerce.service.impl;

import com.ecomlab.ecommerce.common.enums.*;
import com.ecomlab.ecommerce.dto.response.NotificationResponse;
import com.ecomlab.ecommerce.dto.response.PageResponse;
import com.ecomlab.ecommerce.entity.*;
import com.ecomlab.ecommerce.exception.BusinessException;
import com.ecomlab.ecommerce.repository.*;
import com.ecomlab.ecommerce.service.NotificationService;
import com.ecomlab.ecommerce.service.builder.NotificationResponseBuilder;
import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionTemplate;

@Service
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {
  private final NotificationRepository notificationRepository;
  private final JavaMailSender javaMailSender;
  private final TransactionTemplate transactionTemplate;

  @Value("${ecom.notifications.email-enabled:false}")
  private boolean emailEnabled;

  @Value("${ecom.notifications.worker-enabled:true}")
  private boolean workerEnabled;

  @Value("${ecom.notifications.claim-timeout:PT5M}")
  private Duration claimTimeout;

  @Value("${ecom.notifications.batch-size:25}")
  private int batchSize;

  @Value("${ecom.notifications.max-attempts:5}")
  private int maxAttempts;

  @Value("${ecom.notifications.from:no-reply@ecomlab.local}")
  private String from;

  @Transactional(readOnly = true)
  public List<NotificationResponse> getMyNotifications(UUID userId) {
    return notificationRepository.findByUserIdAndIsDeletedFalseOrderByCreatedAtDesc(userId).stream()
        .map(NotificationResponseBuilder::build)
        .toList();
  }

  @Transactional
  public NotificationResponse read(UUID userId, UUID id) {
    NotificationEntity notification =
        notificationRepository
            .findByIdAndUserIdAndIsDeletedFalse(id, userId)
            .orElseThrow(
                () ->
                    new BusinessException(
                        "NOTIFICATION_NOT_FOUND",
                        "NotificationEntity not found",
                        HttpStatus.NOT_FOUND));
    notification.setReadAt(Instant.now());
    return NotificationResponseBuilder.build(notification);
  }

  @Override
  @Transactional
  public void readAll(UUID userId) {
    notificationRepository.markAllRead(userId, Instant.now());
  }

  @Override
  @Transactional(readOnly = true)
  public PageResponse<NotificationResponse> adminPage(String status, int page, int size) {
    PageRequest pageable = PageRequest.of(Math.max(0, page), Math.max(1, Math.min(size, 100)));
    Page<NotificationEntity> result;
    if (status == null || status.isBlank()) {
      result = notificationRepository.findByIsDeletedFalse(pageable);
    } else {
      result =
          notificationRepository.findByStatusAndIsDeletedFalse(
              notificationStatus(status), pageable);
    }

    return PageResponse.<NotificationResponse>builder()
        .content(result.getContent().stream().map(NotificationResponseBuilder::build).toList())
        .page(pageable.getPageNumber())
        .size(pageable.getPageSize())
        .totalElements(result.getTotalElements())
        .build();
  }

  @Override
  @Transactional
  public void queueOrderCreated(OrderEntity order) {
    queue(
        order,
        "Order created " + order.getOrderNumber(),
        "Your order " + order.getOrderNumber() + " was created and is pending payment.");
  }

  @Override
  @Transactional
  public void queuePaymentSucceeded(OrderEntity order) {
    queue(
        order,
        "Payment succeeded " + order.getOrderNumber(),
        "Payment for order " + order.getOrderNumber() + " was confirmed.");
  }

  @Override
  @Transactional
  public void queueShipmentDelivered(OrderEntity order) {
    queue(
        order,
        "Shipment delivered " + order.getOrderNumber(),
        "All shipments for order " + order.getOrderNumber() + " were delivered.");
  }

  @Scheduled(fixedDelayString = "${ecom.notifications.worker-delay:PT10S}")
  public void deliverDueNotifications() {
    if (!workerEnabled) {
      return;
    }

    List<UUID> notificationIds = transactionTemplate.execute(status -> claimDueNotifications());
    if (notificationIds == null) {
      return;
    }

    notificationIds.forEach(
        notificationId ->
            transactionTemplate.executeWithoutResult(
                status -> deliverClaimedNotification(notificationId)));
  }

  @Transactional
  public List<UUID> claimDueNotifications() {
    Instant now = Instant.now();
    notificationRepository.requeueExpiredClaims(now.minus(claimTimeout));
    return notificationRepository
        .lockDueNotifications(NotificationStatus.PENDING, now, PageRequest.of(0, batchSize))
        .stream()
        .map(notification -> claim(notification, now))
        .toList();
  }

  @Transactional
  public void deliverClaimedNotification(UUID notificationId) {
    notificationRepository
        .findById(notificationId)
        .filter(notification -> notification.getStatus() == NotificationStatus.PROCESSING)
        .ifPresent(this::deliver);
  }

  private UUID claim(NotificationEntity notification, Instant now) {
    notification.setStatus(NotificationStatus.PROCESSING);
    notification.setClaimedAt(now);
    notification.setLastError(null);
    return notification.getId();
  }

  private void queue(OrderEntity order, String subject, String body) {
    NotificationEntity notification = new NotificationEntity();
    notification.setUser(order.getUser());
    notification.setOrder(order);
    notification.setRecipientEmail(order.getUser().getEmail());
    notification.setSubject(subject);
    notification.setBody(body);
    notification.setStatus(NotificationStatus.PENDING);
    notification.setAttemptCount(0);
    notification.setNextAttemptAt(Instant.now());
    notificationRepository.save(notification);
  }

  private void deliver(NotificationEntity notification) {
    try {
      if (emailEnabled) {
        send(notification);
      }

      notification.setStatus(NotificationStatus.SENT);
      notification.setClaimedAt(null);
      notification.setLastError(null);
    } catch (RuntimeException exception) {
      retryOrFail(notification, exception);
    }
  }

  private void send(NotificationEntity notification) {
    SimpleMailMessage message = new SimpleMailMessage();
    message.setFrom(from);
    message.setTo(notification.getRecipientEmail());
    message.setSubject(notification.getSubject());
    message.setText(notification.getBody());
    javaMailSender.send(message);
  }

  private void retryOrFail(NotificationEntity notification, RuntimeException exception) {
    int nextAttempt = notification.getAttemptCount() + 1;
    notification.setAttemptCount(nextAttempt);
    notification.setClaimedAt(null);
    notification.setLastError(exception.getMessage());

    if (nextAttempt >= maxAttempts) {
      notification.setStatus(NotificationStatus.FAILED);
      return;
    }

    notification.setStatus(NotificationStatus.PENDING);
    notification.setNextAttemptAt(Instant.now().plus(backoff(nextAttempt)));
  }

  private Duration backoff(int attempt) {
    return Duration.ofSeconds(Math.min(3600, (long) Math.pow(2, attempt) * 30));
  }

  private NotificationStatus notificationStatus(String value) {
    try {
      return NotificationStatus.valueOf(value.toUpperCase());
    } catch (IllegalArgumentException exception) {
      throw new BusinessException(
          "INVALID_NOTIFICATION_STATUS", "Invalid notification status", HttpStatus.BAD_REQUEST);
    }
  }
}
