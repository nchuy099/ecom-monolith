package com.ecomlab.ecommerce.repository;

import com.ecomlab.ecommerce.entity.*;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface AddressRepository extends JpaRepository<UserAddressEntity, UUID> {
  @Query(
      """
      select a from UserAddressEntity a join fetch a.user
      where a.user.id = :userId and a.isDeleted = false
      order by a.defaultAddress desc, a.createdAt desc
      """)
  List<UserAddressEntity> findAllActiveByUserId(@Param("userId") UUID userId);

  @Query(
      """
      select a from UserAddressEntity a join fetch a.user
      where a.id = :id and a.user.id = :userId and a.isDeleted = false
      """)
  java.util.Optional<UserAddressEntity> findOwnedById(
      @Param("id") UUID id, @Param("userId") UUID userId);

  @Modifying
  @Query(
      """
      update UserAddressEntity a set a.defaultAddress = false
      where a.user.id = :userId and a.isDeleted = false and (:exceptId is null or a.id <> :exceptId)
      """)
  void clearDefaultAddress(@Param("userId") UUID userId, @Param("exceptId") UUID exceptId);
}
