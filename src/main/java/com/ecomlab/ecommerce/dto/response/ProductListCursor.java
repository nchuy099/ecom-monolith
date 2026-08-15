package com.ecomlab.ecommerce.dto.response;

import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.UUID;

public record ProductListCursor(
    String keyword,
    UUID categoryId,
    BigDecimal minPrice,
    BigDecimal maxPrice,
    BigDecimal price,
    UUID id) {

  private static final String NULL_VALUE = "-";
  private static final String SEPARATOR = "\\|";

  public static ProductListCursor from(
      ProductListProjection product,
      String keyword,
      UUID categoryId,
      BigDecimal minPrice,
      BigDecimal maxPrice) {
    return new ProductListCursor(
        normalize(keyword), categoryId, minPrice, maxPrice, product.price(), product.variantId());
  }

  public String encode() {
    String raw =
        String.join(
            "|",
            value(keyword),
            value(categoryId),
            value(minPrice),
            value(maxPrice),
            value(price),
            value(id));
    return Base64.getUrlEncoder()
        .withoutPadding()
        .encodeToString(raw.getBytes(StandardCharsets.UTF_8));
  }

  public static ProductListCursor decode(String encoded) {
    String raw = new String(Base64.getUrlDecoder().decode(encoded), StandardCharsets.UTF_8);
    String[] fields = raw.split(SEPARATOR, -1);
    if (fields.length != 6) {
      throw new IllegalArgumentException("Invalid cursor");
    }

    return new ProductListCursor(
        nullableString(fields[0]),
        nullableUuid(fields[1]),
        nullableDecimal(fields[2]),
        nullableDecimal(fields[3]),
        nullableDecimal(fields[4]),
        nullableUuid(fields[5]));
  }

  public boolean matches(
      String keyword, UUID categoryId, BigDecimal minPrice, BigDecimal maxPrice) {
    return same(this.keyword, normalize(keyword))
        && same(this.categoryId, categoryId)
        && same(this.minPrice, minPrice)
        && same(this.maxPrice, maxPrice);
  }

  private static String normalize(String keyword) {
    return keyword == null || keyword.isBlank() ? null : keyword.trim().toLowerCase();
  }

  private static String value(Object value) {
    return value == null ? NULL_VALUE : value.toString();
  }

  private static String nullableString(String value) {
    return NULL_VALUE.equals(value) ? null : value;
  }

  private static UUID nullableUuid(String value) {
    return NULL_VALUE.equals(value) ? null : UUID.fromString(value);
  }

  private static BigDecimal nullableDecimal(String value) {
    return NULL_VALUE.equals(value) ? null : new BigDecimal(value);
  }

  private static boolean same(Object left, Object right) {
    if (left == null) {
      return right == null;
    }

    return left.equals(right);
  }
}
