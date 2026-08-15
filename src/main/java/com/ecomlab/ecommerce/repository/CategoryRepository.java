package com.ecomlab.ecommerce.repository;

import com.ecomlab.ecommerce.entity.CategoryEntity;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface CategoryRepository extends JpaRepository<CategoryEntity, UUID> {
  @Query(
      """
      select distinct c
      from CategoryEntity c
      left join fetch c.parent
      where c.isDeleted = false
      order by c.name
      """)
  List<CategoryEntity> findAllActive();

  Optional<CategoryEntity> findByIdAndIsDeletedFalse(UUID id);
}
