package com.ecomlab.ecommerce;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableCaching
@EnableScheduling
@SpringBootApplication
public class EcomMonolithJavaApplication {
  public static void main(String[] args) {
    SpringApplication.run(EcomMonolithJavaApplication.class, args);
  }
}
