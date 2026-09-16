package com.ecomlab.ecommerce.service.media;

import com.ecomlab.ecommerce.config.R2Properties;
import com.ecomlab.ecommerce.dto.response.MediaUploadResponse;
import com.ecomlab.ecommerce.exception.BusinessException;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.Locale;
import java.util.Optional;
import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

@Service
public class R2StorageService {
  private static final String DEFAULT_CONTENT_TYPE = "image/jpeg";
  private final Optional<S3Client> client;
  private final R2Properties properties;
  private final HttpClient httpClient;

  public R2StorageService(Optional<S3Client> client, R2Properties properties) {
    this.client = client;
    this.properties = properties;
    this.httpClient =
        HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(8))
            .followRedirects(HttpClient.Redirect.NEVER)
            .build();
  }

  public MediaUploadResponse upload(MultipartFile file) {
    if (file == null || file.isEmpty()) {
      throw invalid("IMAGE_EMPTY", "Vui lòng chọn một ảnh sản phẩm");
    }
    String contentType = normalizeContentType(file.getContentType());
    validateContentType(contentType);
    if (file.getSize() > properties.getMaxImageBytes()) {
      throw invalid("IMAGE_TOO_LARGE", "Ảnh vượt quá giới hạn 10 MB");
    }
    try {
      return put(file.getBytes(), contentType);
    } catch (IOException exception) {
      throw new BusinessException(
          "IMAGE_READ_FAILED", "Không thể đọc file ảnh", HttpStatus.BAD_REQUEST);
    }
  }

  public MediaUploadResponse importFromUrl(String rawUrl) {
    ensureConfigured();
    URI uri;
    try {
      uri = URI.create(rawUrl.trim());
    } catch (IllegalArgumentException exception) {
      throw invalid("IMAGE_URL_INVALID", "URL ảnh không hợp lệ");
    }
    validateRemoteUri(uri);
    try {
      HttpResponse<InputStream> response =
          httpClient.send(
              HttpRequest.newBuilder(uri).timeout(Duration.ofSeconds(20)).GET().build(),
              HttpResponse.BodyHandlers.ofInputStream());
      if (response.statusCode() < 200 || response.statusCode() >= 300) {
        response.body().close();
        throw invalid("IMAGE_URL_FETCH_FAILED", "Không thể tải ảnh từ URL đã nhập");
      }
      String contentType =
          normalizeContentType(response.headers().firstValue("content-type").orElse(null));
      validateContentType(contentType);
      long contentLength = response.headers().firstValueAsLong("content-length").orElse(-1L);
      if (contentLength > properties.getMaxImageBytes()) {
        response.body().close();
        throw invalid("IMAGE_TOO_LARGE", "Ảnh vượt quá giới hạn 10 MB");
      }
      byte[] bytes = readLimited(response.body());
      return put(bytes, contentType);
    } catch (IOException exception) {
      throw new BusinessException(
          "IMAGE_URL_FETCH_FAILED", "Không thể tải ảnh từ URL đã nhập", HttpStatus.BAD_REQUEST);
    } catch (InterruptedException exception) {
      Thread.currentThread().interrupt();
      throw new BusinessException(
          "IMAGE_URL_FETCH_INTERRUPTED",
          "Tải ảnh từ URL bị gián đoạn",
          HttpStatus.BAD_REQUEST);
    }
  }

  public boolean isR2Url(String imageUrl) {
    return imageUrl != null
        && !imageUrl.isBlank()
        && !properties.getPublicBaseUrl().isBlank()
        && imageUrl.startsWith(trimTrailingSlash(properties.getPublicBaseUrl()) + "/");
  }

  public void ensureAvailable() {
    ensureConfigured();
  }

  private MediaUploadResponse put(byte[] bytes, String contentType) {
    ensureConfigured();
    String extension = extension(contentType);
    String key = "products/" + UUID.randomUUID() + "." + extension;
    client
        .orElseThrow(() -> unavailable())
        .putObject(
            PutObjectRequest.builder()
                .bucket(properties.getBucket())
                .key(key)
                .contentType(contentType)
                .cacheControl("public, max-age=31536000, immutable")
                .build(),
            RequestBody.fromBytes(bytes));
    return new MediaUploadResponse(
        trimTrailingSlash(properties.getPublicBaseUrl()) + "/" + key,
        key,
        contentType,
        bytes.length);
  }

  private byte[] readLimited(InputStream input) throws IOException {
    try (input) {
      ByteArrayOutputStream output = new ByteArrayOutputStream();
      byte[] buffer = new byte[8192];
      int total = 0;
      int read;
      while ((read = input.read(buffer)) != -1) {
        total += read;
        if (total > properties.getMaxImageBytes()) {
          throw new BusinessException("IMAGE_TOO_LARGE", "Ảnh vượt quá giới hạn 10 MB", HttpStatus.BAD_REQUEST);
        }
        output.write(buffer, 0, read);
      }
      return output.toByteArray();
    }
  }

  private void ensureConfigured() {
    if (!properties.isEnabled() || client.isEmpty()) {
      throw unavailable();
    }
  }

  private BusinessException unavailable() {
    return new BusinessException(
        "R2_NOT_CONFIGURED", "Cloudflare R2 chưa được cấu hình", HttpStatus.SERVICE_UNAVAILABLE);
  }

  private void validateRemoteUri(URI uri) {
    if (!("http".equalsIgnoreCase(uri.getScheme()) || "https".equalsIgnoreCase(uri.getScheme()))
        || uri.getHost() == null
        || uri.getUserInfo() != null) {
      throw invalid("IMAGE_URL_INVALID", "Chỉ hỗ trợ URL HTTP/HTTPS hợp lệ");
    }
    try {
      for (InetAddress address : InetAddress.getAllByName(uri.getHost())) {
        if (address.isAnyLocalAddress()
            || address.isLoopbackAddress()
            || address.isLinkLocalAddress()
            || address.isSiteLocalAddress()) {
          throw invalid("IMAGE_URL_BLOCKED", "URL ảnh trỏ tới địa chỉ mạng nội bộ");
        }
      }
    } catch (IOException exception) {
      throw invalid("IMAGE_URL_INVALID", "Không thể phân giải host của URL ảnh");
    }
  }

  private void validateContentType(String contentType) {
    if (!SetOfImageTypes.contains(contentType)) {
      throw invalid("IMAGE_TYPE_UNSUPPORTED", "Chỉ hỗ trợ ảnh JPEG, PNG, WebP hoặc AVIF");
    }
  }

  private static String normalizeContentType(String contentType) {
    if (contentType == null || contentType.isBlank()) {
      return DEFAULT_CONTENT_TYPE;
    }
    return contentType.split(";", 2)[0].trim().toLowerCase(Locale.ROOT);
  }

  private static String extension(String contentType) {
    return switch (contentType) {
      case "image/png" -> "png";
      case "image/webp" -> "webp";
      case "image/avif" -> "avif";
      default -> "jpg";
    };
  }

  private static String trimTrailingSlash(String value) {
    return value.replaceAll("/+$", "");
  }

  private static BusinessException invalid(String code, String message) {
    return new BusinessException(code, message, HttpStatus.BAD_REQUEST);
  }

  private static final class SetOfImageTypes {
    private static boolean contains(String contentType) {
      return "image/jpeg".equals(contentType)
          || "image/png".equals(contentType)
          || "image/webp".equals(contentType)
          || "image/avif".equals(contentType);
    }
  }
}
