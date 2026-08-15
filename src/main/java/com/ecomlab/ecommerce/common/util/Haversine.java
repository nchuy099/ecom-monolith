package com.ecomlab.ecommerce.common.util;

import java.math.BigDecimal;

public final class Haversine {
  private static final double EARTH_RADIUS_KM = 6371.0088;

  private Haversine() {}

  public static double kilometers(
      BigDecimal lat1, BigDecimal lon1, BigDecimal lat2, BigDecimal lon2) {
    double dLat = Math.toRadians(lat2.doubleValue() - lat1.doubleValue());
    double dLon = Math.toRadians(lon2.doubleValue() - lon1.doubleValue());
    double a =
        Math.pow(Math.sin(dLat / 2), 2)
            + Math.cos(Math.toRadians(lat1.doubleValue()))
                * Math.cos(Math.toRadians(lat2.doubleValue()))
                * Math.pow(Math.sin(dLon / 2), 2);
    return EARTH_RADIUS_KM * 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
  }
}
