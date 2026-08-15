package com.ecomlab.ecommerce.config;

import java.time.*;
import java.util.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.*;
import org.springframework.stereotype.Service;

@Service
public class AccessTokenService {

  private final JwtEncoder encoder;
  private final Duration ttl;

  public AccessTokenService(
      JwtEncoder encoder, @Value("${ecom.jwt.access-token-ttl}") Duration ttl) {
    this.encoder = encoder;
    this.ttl = ttl;
  }

  public String issue(UUID userId, String role) {
    Instant now = Instant.now();
    JwtClaimsSet claims =
        JwtClaimsSet.builder()
            .subject(userId.toString())
            .issuedAt(now)
            .expiresAt(now.plus(ttl))
            .claim("roles", List.of(role))
            .build();
    return encoder
        .encode(
            JwtEncoderParameters.from(
                JwsHeader.with(MacAlgorithm.HS256).type("JWT").build(), claims))
        .getTokenValue();
  }
}
