package com.ecomlab.ecommerce.repository;

import com.ecomlab.ecommerce.entity.*;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TrackingEventRepository extends JpaRepository<TrackingEventEntity, UUID> {}
