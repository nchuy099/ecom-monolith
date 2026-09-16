package com.ecomlab.ecommerce.service.media;

import com.ecomlab.ecommerce.dto.response.MediaMigrationResponse;
import com.ecomlab.ecommerce.entity.ProductVariantEntity;
import com.ecomlab.ecommerce.repository.ProductVariantRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class R2MediaMigrationService {
  private final ProductVariantRepository productVariantRepository;
  private final R2StorageService storageService;

  public MediaMigrationResponse migrateExistingImages() {
    storageService.ensureAvailable();
    List<ProductVariantEntity> variants = productVariantRepository.findAll();
    int migrated = 0;
    int skipped = 0;
    int failed = 0;
    for (ProductVariantEntity variant : variants) {
      String imageUrl = variant.getImageUrl();
      if (imageUrl == null || imageUrl.isBlank() || storageService.isR2Url(imageUrl)) {
        skipped++;
        continue;
      }
      try {
        variant.setImageUrl(storageService.importFromUrl(imageUrl).imageUrl());
        productVariantRepository.save(variant);
        migrated++;
      } catch (RuntimeException exception) {
        failed++;
        log.warn("Could not migrate product variant image {}", variant.getId(), exception);
      }
    }
    return new MediaMigrationResponse(variants.size(), migrated, skipped, failed);
  }
}
