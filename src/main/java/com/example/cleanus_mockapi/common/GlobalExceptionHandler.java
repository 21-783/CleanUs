package com.example.cleanus_mockapi.common;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.ErrorResponseException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;

// 전역 예외 처리 파일
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiError> badRequest(MethodArgumentNotValidException ex, HttpServletRequest req) {
        ApiError body = new ApiError("BAD_REQUEST",
                ex.getBindingResult().getAllErrors().get(0).getDefaultMessage(),
                req.getHeader("X-Request-Id"),
                Instant.now());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
    }

    @ExceptionHandler(ErrorResponseException.class)
    public ResponseEntity<ApiError> springErrors(ErrorResponseException ex, HttpServletRequest req) {
        ApiError body = new ApiError(ex.getStatusCode().toString(),
                ex.getBody().getDetail(),
                req.getHeader("X-Request-Id"),
                Instant.now());
        return ResponseEntity.status(ex.getStatusCode()).body(body);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiError> unknown(Exception ex, HttpServletRequest req) {
        ApiError body = new ApiError("INTERNAL_ERROR",
                ex.getMessage(),
                req.getHeader("X-Request-Id"),
                Instant.now());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(body);
    }
}
