package com.ecomlab.ecommerce.entity;

import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import java.time.Instant;
import java.util.UUID;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
public abstract class BaseEntity {
  @CreatedDate
  @Column(name = "created_at", nullable = false, updatable = false)
  protected Instant createdAt;

  @CreatedBy
  @Column(name = "created_by", updatable = false)
  protected UUID createdBy;

  @LastModifiedDate
  @Column(name = "updated_at", nullable = false)
  protected Instant updatedAt;

  @LastModifiedBy
  @Column(name = "updated_by")
  protected UUID updatedBy;

  @Column(name = "is_deleted", nullable = false)
  protected boolean isDeleted;

  @PrePersist
  protected void created() {
    createdAt = updatedAt = Instant.now();
  }

  @PreUpdate
  protected void updated() {
    updatedAt = Instant.now();
  }

  public void softDelete() {
    isDeleted = true;
  }
}
