package com.ecomlab.ecommerce.controller;

import com.ecomlab.ecommerce.dto.request.ImportImageRequest;
import com.ecomlab.ecommerce.dto.response.MediaMigrationResponse;
import com.ecomlab.ecommerce.dto.response.MediaUploadResponse;
import com.ecomlab.ecommerce.service.media.R2MediaMigrationService;
import com.ecomlab.ecommerce.service.media.R2StorageService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/v1/admin/media/images")
@PreAuthorize("hasRole('ADMIN')")
@RequiredArgsConstructor
public class AdminMediaController {
  private final R2StorageService storageService;
  private final R2MediaMigrationService migrationService;

  @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  public ResponseEntity<MediaUploadResponse> upload(@RequestPart("file") MultipartFile file) {
    return ResponseEntity.status(HttpStatus.CREATED).body(storageService.upload(file));
  }

  @PostMapping("/import")
  public ResponseEntity<MediaUploadResponse> importFromUrl(
      @Valid @RequestBody ImportImageRequest request) {
    return ResponseEntity.status(HttpStatus.CREATED)
        .body(storageService.importFromUrl(request.url()));
  }

  @PostMapping("/migrate-existing")
  public ResponseEntity<MediaMigrationResponse> migrateExisting() {
    return ResponseEntity.ok(migrationService.migrateExistingImages());
  }
}
