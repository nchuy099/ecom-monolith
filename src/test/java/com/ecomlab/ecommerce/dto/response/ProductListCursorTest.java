package com.ecomlab.ecommerce.dto.response;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.math.BigDecimal;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class ProductListCursorTest {
  @Test
  void encoded_cursor_round_trips_and_matches_same_filter() {
    UUID categoryId = UUID.randomUUID();
    UUID productId = UUID.randomUUID();
    UUID variantId = UUID.randomUUID();
    ProductListProjection product =
        new ProductListProjection(
            productId,
            "Phone",
            categoryId,
            true,
            variantId,
            "SKU-1",
            "Phone Black",
            new BigDecimal("199.99"),
            10,
            2);

    ProductListCursor cursor =
        ProductListCursor.from(
            product, " phone ", categoryId, new BigDecimal("100.00"), new BigDecimal("300.00"));
    ProductListCursor decoded = ProductListCursor.decode(cursor.encode());

    assertThat(decoded.price()).isEqualByComparingTo("199.99");
    assertThat(decoded.id()).isEqualTo(variantId);
    assertThat(
            decoded.matches(
                "phone", categoryId, new BigDecimal("100.00"), new BigDecimal("300.00")))
        .isTrue();
  }

  @Test
  void decode_rejects_invalid_cursor() {
    assertThatThrownBy(() -> ProductListCursor.decode("not-a-valid-cursor"))
        .isInstanceOf(IllegalArgumentException.class);
  }
}
