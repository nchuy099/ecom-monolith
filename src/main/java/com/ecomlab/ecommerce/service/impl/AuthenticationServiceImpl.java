package com.ecomlab.ecommerce.service.impl;

import com.ecomlab.ecommerce.common.enums.*;
import com.ecomlab.ecommerce.config.AccessTokenService;
import com.ecomlab.ecommerce.dto.response.TokenPairResponse;
import com.ecomlab.ecommerce.entity.*;
import com.ecomlab.ecommerce.exception.BusinessException;
import com.ecomlab.ecommerce.repository.*;
import com.ecomlab.ecommerce.service.AuthenticationService;
import com.ecomlab.ecommerce.service.builder.TokenPairResponseBuilder;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.*;
import java.util.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthenticationServiceImpl implements AuthenticationService {
  private final UserRepository userRepository;
  private final RefreshTokenRepository refreshTokenRepository;
  private final PasswordEncoder passwordEncoder;
  private final AccessTokenService accessTokenService;
  private final Duration refreshTtl;

  public AuthenticationServiceImpl(
      UserRepository userRepository,
      RefreshTokenRepository refreshTokenRepository,
      PasswordEncoder passwordEncoder,
      AccessTokenService accessTokenService,
      @Value("${ecom.jwt.refresh-token-ttl}") Duration refreshTtl) {
    this.userRepository = userRepository;
    this.refreshTokenRepository = refreshTokenRepository;
    this.passwordEncoder = passwordEncoder;
    this.accessTokenService = accessTokenService;
    this.refreshTtl = refreshTtl;
  }

  @Transactional
  public TokenPairResponse register(
      String email, String password, String displayName, String device) {
    if (userRepository.findByEmailAndIsDeletedFalse(email).isPresent())
      throw new BusinessException(
          "EMAIL_ALREADY_REGISTERED", "Email already registered", HttpStatus.CONFLICT);
    return issue(
        userRepository.save(
            UserEntity.builder()
                .email(email.toLowerCase(Locale.ROOT))
                .passwordHash(passwordEncoder.encode(password))
                .displayName(displayName)
                .role(Role.CUSTOMER)
                .build()),
        device,
        UUID.randomUUID());
  }

  @Transactional
  public TokenPairResponse login(String email, String password, String device) {
    UserEntity user =
        userRepository
            .findByEmailAndIsDeletedFalse(email.toLowerCase(Locale.ROOT))
            .orElseThrow(
                () ->
                    new BusinessException(
                        "INVALID_CREDENTIALS", "Invalid credentials", HttpStatus.UNAUTHORIZED));
    if (!passwordEncoder.matches(password, user.getPasswordHash()))
      throw new BusinessException(
          "INVALID_CREDENTIALS", "Invalid credentials", HttpStatus.UNAUTHORIZED);
    return issue(user, device, UUID.randomUUID());
  }

  @Transactional
  public TokenPairResponse refresh(String rawToken, String device) {
    RefreshTokenEntity old =
        refreshTokenRepository
            .findByTokenHashAndIsDeletedFalse(hash(rawToken))
            .orElseThrow(
                () ->
                    new BusinessException(
                        "INVALID_REFRESH_TOKEN", "Invalid refresh token", HttpStatus.UNAUTHORIZED));
    if (!old.active(Instant.now())) {
      revokeFamily(old.getFamilyId());
      throw new BusinessException(
          "REFRESH_TOKEN_REUSED", "Expired or reused refresh token", HttpStatus.UNAUTHORIZED);
    }
    old.setRevokedAt(Instant.now());
    TokenPairResponse next = issue(old.getUser(), device, old.getFamilyId());
    old.setReplacedByTokenId(
        refreshTokenRepository
            .findByTokenHashAndIsDeletedFalse(hash(next.getRefreshToken()))
            .orElseThrow()
            .getId());
    return next;
  }

  @Transactional
  public void logout(String rawToken) {
    refreshTokenRepository
        .findByTokenHashAndIsDeletedFalse(hash(rawToken))
        .ifPresent(t -> revokeFamily(t.getFamilyId()));
  }

  private TokenPairResponse issue(UserEntity user, String device, UUID family) {
    String raw = UUID.randomUUID() + "." + UUID.randomUUID();
    RefreshTokenEntity token = new RefreshTokenEntity();
    token.setUser(user);
    token.setTokenHash(hash(raw));
    token.setFamilyId(family);
    token.setDeviceName(device);
    token.setExpiresAt(Instant.now().plus(refreshTtl));
    refreshTokenRepository.save(token);
    return TokenPairResponseBuilder.build(
        accessTokenService.issue(user.getId(), user.getRole().name()), raw);
  }

  private void revokeFamily(UUID family) {
    refreshTokenRepository.findAll().stream()
        .filter(t -> t.getFamilyId().equals(family) && t.getRevokedAt() == null)
        .forEach(t -> t.setRevokedAt(Instant.now()));
  }

  private String hash(String value) {
    try {
      return HexFormat.of()
          .formatHex(
              MessageDigest.getInstance("SHA-256").digest(value.getBytes(StandardCharsets.UTF_8)));
    } catch (Exception e) {
      throw new IllegalStateException(e);
    }
  }
}
