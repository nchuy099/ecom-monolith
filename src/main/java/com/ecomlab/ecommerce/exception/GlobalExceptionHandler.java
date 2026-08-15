package com.ecomlab.ecommerce.exception;

import jakarta.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.*;
import org.springframework.http.*;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

@RestControllerAdvice
public class GlobalExceptionHandler {
  @ExceptionHandler(AccessDeniedException.class)
  ResponseEntity<ErrorResponse> accessDenied(HttpServletRequest request) {
    return error(
        HttpStatus.FORBIDDEN,
        "ACCESS_DENIED",
        "You do not have permission to access this resource",
        request);
  }

  @ExceptionHandler(MethodArgumentNotValidException.class)
  ResponseEntity<Map<String, String>> validation(MethodArgumentNotValidException exception) {
    Map<String, String> errors = new HashMap<>();
    exception
        .getBindingResult()
        .getFieldErrors()
        .forEach(e -> errors.put(e.getField(), e.getDefaultMessage()));
    return ResponseEntity.badRequest().body(errors);
  }

  @ExceptionHandler(BusinessException.class)
  ResponseEntity<ErrorResponse> business(BusinessException exception, HttpServletRequest request) {
    return error(exception.getStatus(), exception.getCode(), exception.getMessage(), request);
  }

  @ExceptionHandler(Exception.class)
  ResponseEntity<ErrorResponse> unexpected(Exception exception, HttpServletRequest request) {
    return error(
        HttpStatus.INTERNAL_SERVER_ERROR,
        "INTERNAL_SERVER_ERROR",
        "Unexpected server error",
        request);
  }

  private ResponseEntity<ErrorResponse> error(
      HttpStatus status, String code, String message, HttpServletRequest request) {
    return ResponseEntity.status(status)
        .body(
            new ErrorResponse(
                LocalDateTime.now(), status.value(), code, message, request.getRequestURI()));
  }
}
