package com.ecomlab.ecommerce.config;

import java.net.URI;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;

@Configuration
@EnableConfigurationProperties(R2Properties.class)
public class R2Configuration {
  @Bean(destroyMethod = "close")
  @ConditionalOnProperty(name = "ecom.r2.enabled", havingValue = "true")
  S3Client r2Client(R2Properties properties) {
    if (properties.getAccountId().isBlank()
        || properties.getAccessKeyId().isBlank()
        || properties.getSecretAccessKey().isBlank()
        || properties.getBucket().isBlank()
        || properties.getPublicBaseUrl().isBlank()) {
      throw new IllegalStateException(
          "R2 is enabled but account, credentials, bucket and public base URL are incomplete");
    }

    return S3Client.builder()
        .endpointOverride(
            URI.create("https://" + properties.getAccountId() + ".r2.cloudflarestorage.com"))
        .region(Region.of("auto"))
        .credentialsProvider(
            StaticCredentialsProvider.create(
                AwsBasicCredentials.create(
                    properties.getAccessKeyId(), properties.getSecretAccessKey())))
        .build();
  }
}
