package com.ecomlab.ecommerce.common.util;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import org.junit.jupiter.api.Test;

class HaversineTest {
  @Test
  void distance_is_zero_for_same_coordinate_and_is_symmetric() {
    var hanoiLat = new BigDecimal("21.028511");
    var hanoiLon = new BigDecimal("105.804817");
    var hcmLat = new BigDecimal("10.823099");
    var hcmLon = new BigDecimal("106.629664");
    assertThat(Haversine.kilometers(hanoiLat, hanoiLon, hanoiLat, hanoiLon)).isZero();
    assertThat(Haversine.kilometers(hanoiLat, hanoiLon, hcmLat, hcmLon))
        .isCloseTo(
            Haversine.kilometers(hcmLat, hcmLon, hanoiLat, hanoiLon),
            org.assertj.core.data.Offset.offset(0.0001));
  }
}
