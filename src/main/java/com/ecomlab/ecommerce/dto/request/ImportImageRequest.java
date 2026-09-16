package com.ecomlab.ecommerce.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ImportImageRequest(
    @NotBlank @Size(max = 2000) String url) {}
