package com.ecomlab.ecommerce.service.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.ecomlab.ecommerce.dto.response.CursorPageResponse;
import com.ecomlab.ecommerce.dto.response.ProductListCursor;
import com.ecomlab.ecommerce.dto.response.ProductListProjection;
import com.ecomlab.ecommerce.dto.response.ProductSummaryResponse;
import com.ecomlab.ecommerce.exception.BusinessException;
import com.ecomlab.ecommerce.repository.ProductVariantRepository;
import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ProductCatalogServiceImplTest {
  @Mock private ProductVariantRepository productVariantRepository;

  @Test
  void search_uses_page_size_plus_one_and_builds_next_cursor() {
    ProductCatalogServiceImpl service = new ProductCatalogServiceImpl(productVariantRepository);
    UUID categoryId = UUID.randomUUID();
    ProductListProjection first = product("199.99");
    ProductListProjection second = product("299.99");

    when(productVariantRepository.findCatalogPageAfterCursor(
            eq("phone"),
            eq(categoryId),
            eq(new BigDecimal("100.00")),
            eq(new BigDecimal("500.00")),
            eq(null),
            eq(2)))
        .thenReturn(List.of(first, second));

    CursorPageResponse<ProductSummaryResponse> response =
        service.search(
            " Phone ", categoryId, new BigDecimal("100.00"), new BigDecimal("500.00"), null, 1);

    assertThat(response.isHasNext()).isTrue();
    assertThat(response.getContent()).hasSize(1);
    assertThat(response.getNextCursor()).isNotBlank();
    assertThat(ProductListCursor.decode(response.getNextCursor()).id())
        .isEqualTo(first.variantId());
  }

  @Test
  void search_rejects_cursor_from_different_filter() {
    ProductCatalogServiceImpl service = new ProductCatalogServiceImpl(productVariantRepository);
    UUID categoryId = UUID.randomUUID();
    ProductListCursor cursor =
        ProductListCursor.from(product("199.99"), "phone", categoryId, null, null);

    assertThatThrownBy(() -> service.search("laptop", categoryId, null, null, cursor.encode(), 10))
        .isInstanceOf(BusinessException.class)
        .hasMessageContaining("Cursor does not match");

    verify(productVariantRepository, never())
        .findCatalogPageAfterCursor(
            eq("laptop"), eq(categoryId), eq(null), eq(null), eq(cursor), eq(11));
  }

  private ProductListProjection product(String price) {
    return new ProductListProjection(
        UUID.randomUUID(),
        "Phone",
        UUID.randomUUID(),
        true,
        UUID.randomUUID(),
        "SKU-1",
        "Black",
        new BigDecimal(price),
        10,
        0);
  }
}
