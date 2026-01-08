package com.example.crudjob.exception;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.example.crudjob.dto.response.ApiRes;
import com.example.crudjob.utils.AppConstants;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

        /* ========= 404 ========= */
        /**
         * Xử lý exception khi resource không được tìm thấy (404)
         * 
         * @param ex ResourceNotFoundException
         * @return ResponseEntity với status 404
         */
        @ExceptionHandler(ResourceNotFoundException.class)
        public ResponseEntity<ApiRes<Void>> handleNotFound(
                        ResourceNotFoundException ex) {
                log.warn("Resource not found: {}", ex.getMessage());
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                                .body(ApiRes.error(
                                                ex.getMessage(),
                                                HttpStatus.NOT_FOUND.value()));
        }

        /* ========= 400 ========= */
        /**
         * Xử lý exception BadRequestException (400)
         * 
         * @param ex BadRequestException
         * @return ResponseEntity với status 400
         */
        @ExceptionHandler(BadRequestException.class)
        public ResponseEntity<ApiRes<Void>> handleBadRequest(
                        BadRequestException ex) {
                log.warn("Bad request: {}", ex.getMessage());
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                                .body(ApiRes.error(
                                                ex.getMessage(),
                                                HttpStatus.BAD_REQUEST.value()));
        }

        /* ========= ENCRYPTION/DECRYPTION ERRORS ========= */
        /**
         * Xử lý exception khi encryption thất bại
         * 
         * @param ex EncryptionException
         * @return ResponseEntity với status 500
         */
        @ExceptionHandler(EncryptionException.class)
        public ResponseEntity<ApiRes<Void>> handleEncryptionException(
                        EncryptionException ex) {
                log.error("Encryption error: {}", ex.getMessage(), ex.getCause());
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                .body(ApiRes.error(
                                                AppConstants.ENCRYPTION_ERROR,
                                                HttpStatus.INTERNAL_SERVER_ERROR.value()));
        }

        /**
         * Xử lý exception khi decryption thất bại
         * 
         * @param ex DecryptionException
         * @return ResponseEntity với status 500
         */
        @ExceptionHandler(DecryptionException.class)
        public ResponseEntity<ApiRes<Void>> handleDecryptionException(
                        DecryptionException ex) {
                log.error("Decryption error: {}", ex.getMessage(), ex.getCause());
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                .body(ApiRes.error(
                                                AppConstants.DECRYPTION_ERROR,
                                                HttpStatus.INTERNAL_SERVER_ERROR.value()));
        }

        /**
         * Xử lý exception khi encrypted data format không hợp lệ
         * 
         * @param ex InvalidEncryptedDataException
         * @return ResponseEntity với status 400
         */
        @ExceptionHandler(InvalidEncryptedDataException.class)
        public ResponseEntity<ApiRes<Void>> handleInvalidEncryptedData(
                        InvalidEncryptedDataException ex) {
                log.warn("Invalid encrypted data format: {}", ex.getMessage());
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                                .body(ApiRes.error(
                                                ex.getMessage(),
                                                HttpStatus.BAD_REQUEST.value()));
        }

        /* ========= VALIDATION ========= */
        /**
         * Xử lý exception validation khi request body không hợp lệ
         * 
         * @param ex MethodArgumentNotValidException
         * @return ResponseEntity với status 400 và chi tiết lỗi validation
         */
        @ExceptionHandler(MethodArgumentNotValidException.class)
        public ResponseEntity<ApiRes<Map<String, String>>> handleValidation(
                        MethodArgumentNotValidException ex) {
                Map<String, String> errors = new HashMap<>();

                ex.getBindingResult()
                                .getFieldErrors()
                                .forEach(error -> errors.put(
                                                error.getField(),
                                                error.getDefaultMessage()));

                log.warn("Validation failed: {}", errors);
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                                .body(ApiRes.<Map<String, String>>builder()
                                                .success(false)
                                                .status(400)
                                                .message(AppConstants.VALIDATION_FAILED)
                                                .data(errors)
                                                .timestamp(java.time.LocalDateTime.now())
                                                .build());
        }

        /* ========= JPA VALIDATION ========= */
        /**
         * Xử lý exception validation từ JPA/Hibernate (ConstraintViolationException)
         * Xảy ra khi validate entity trước khi persist
         * 
         * @param ex ConstraintViolationException
         * @return ResponseEntity với status 400 và chi tiết lỗi validation
         */
        @ExceptionHandler(ConstraintViolationException.class)
        public ResponseEntity<ApiRes<Map<String, String>>> handleConstraintViolation(
                        ConstraintViolationException ex) {
                Map<String, String> errors = new HashMap<>();

                for (ConstraintViolation<?> violation : ex.getConstraintViolations()) {
                        String field = violation.getPropertyPath().toString();
                        String message = violation.getMessage();
                        errors.put(field, message);
                }

                log.warn("Constraint violation: {}", errors);
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                                .body(ApiRes.<Map<String, String>>builder()
                                                .success(false)
                                                .status(400)
                                                .message(AppConstants.VALIDATION_FAILED)
                                                .data(errors)
                                                .timestamp(java.time.LocalDateTime.now())
                                                .build());
        }

        /* ========= 500 ========= */
        /**
         * Xử lý exception không mong đợi (500)
         * 
         * @param ex Exception
         * @return ResponseEntity với status 500
         */
        @ExceptionHandler(Exception.class)
        public ResponseEntity<ApiRes<Void>> handleOther(Exception ex) {
                log.error("Unexpected error: {}", ex.getMessage(), ex);
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                .body(ApiRes.error(
                                                AppConstants.INTERNAL_SERVER_ERROR,
                                                500));
        }
}