package com.ecomlab.ecommerce.dto.response;

public record MediaUploadResponse(String imageUrl, String objectKey, String contentType, long size) {}
