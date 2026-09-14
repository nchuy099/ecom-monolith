package com.ecomlab.ecommerce.repository;

import com.ecomlab.ecommerce.common.enums.Role;
import com.ecomlab.ecommerce.entity.*;
import java.util.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, UUID> {
  Optional<UserEntity> findByEmailAndIsDeletedFalse(String email);

  @Query(
      """
      select u
      from UserEntity u
      where u.role = :role
        and u.isDeleted = false
      order by u.displayName asc
      """)
  List<UserEntity> findActiveByRole(@Param("role") Role role);
}
