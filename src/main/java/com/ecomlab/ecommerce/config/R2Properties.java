package com.ecomlab.ecommerce.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@ConfigurationProperties(prefix = "ecom.r2")
public class R2Properties {
  private boolean enabled;
  private String accountId;
  private String accessKeyId;
  private String secretAccessKey;
  private String bucket;
  private String publicBaseUrl;
  private long maxImageBytes = 10 * 1024 * 1024;
}
