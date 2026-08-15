package com.ecomlab.ecommerce.config;

import com.github.benmanes.caffeine.cache.Caffeine;
import java.time.Duration;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.CacheManager;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.*;

@Configuration
public class CacheConfiguration {

  @Bean
  CacheManager cacheManager(
      @Value("${ecom.cache.product-detail-ttl}") Duration ttl,
      @Value("${ecom.cache.product-detail-max-size}") long maxSize) {
    CaffeineCacheManager manager = new CaffeineCacheManager("productDetail");
    manager.setCaffeine(
        Caffeine.newBuilder().expireAfterWrite(ttl).maximumSize(maxSize).recordStats());
    return manager;
  }
}
