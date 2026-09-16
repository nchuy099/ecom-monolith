package com.ecomlab.ecommerce.dto.response;

public record MediaMigrationResponse(int scanned, int migrated, int skipped, int failed) {}
