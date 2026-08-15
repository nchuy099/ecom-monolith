package com.ecomlab.ecommerce.repository;

import com.ecomlab.ecommerce.entity.*;
import java.util.*;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface CartRepository extends JpaRepository<CartEntity, UUID> {
  @Query(
      """
      select c
      from CartEntity c
      left join fetch c.items i
      left join fetch i.variant
      where c.user.id = :userId
        and c.isDeleted = false
      """)
  Optional<CartEntity> findCurrentByUserId(@Param("userId") UUID userId);
}
