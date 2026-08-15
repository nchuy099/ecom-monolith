package com.ecomlab.ecommerce.config;

import java.util.Optional;
import java.util.UUID;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.security.core.context.SecurityContextHolder;

@Configuration
@EnableJpaAuditing(auditorAwareRef = "currentAuditor")
public class AuditingConfiguration {

  @Bean("currentAuditor")
  AuditorAware<UUID> currentAuditor() {
    return () -> {
      var authentication = SecurityContextHolder.getContext().getAuthentication();
      if (authentication == null
          || !authentication.isAuthenticated()
          || "anonymousUser".equals(authentication.getName())) {
        return Optional.empty();
      }
      try {
        return Optional.of(UUID.fromString(authentication.getName()));
      } catch (IllegalArgumentException ignored) {
        return Optional.empty();
      }
    };
  }
}
