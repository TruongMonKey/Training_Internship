package com.example.crudjob.exception;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.example.crudjob.constant.AppConstants;
import com.example.crudjob.dto.response.ApiRes;

import jakarta.validation.ConstraintViolationException;

@RestControllerAdvice
public class GlobalExceptionHandler {
        private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

        /* ========= 404 ========= */
        /**
         * Xử lý exception khi resource không được tìm thấy (404)
         * 
         * @param ex ResourceNotFoundException
         * @return ResponseEntity với status 404
         */
        @ExceptionHandler(ResourceNotFoundException.class)
        public ResponseEntity<ApiRes<Void>> handleNotFound(ResourceNotFoundException ex) {
                logger.warn("HANDLER_404: Resource not found - message: {}", ex.getMessage());
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                                .body(ApiRes.error(ex.getMessage(), HttpStatus.NOT_FOUND.value()));
        }

        /* ========= 400 ========= */
        /**
         * Xử lý exception BadRequestException (400)
         * 
         * @param ex BadRequestException
         * @return ResponseEntity với status 400
         */
        @ExceptionHandler(BadRequestException.class)
        public ResponseEntity<ApiRes<Void>> handleBadRequest(BadRequestException ex) {
                logger.warn("HANDLER_400: Bad request - message: {}", ex.getMessage());
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                                .body(ApiRes.error(ex.getMessage(), HttpStatus.BAD_REQUEST.value()));
        }

        /* ========= ENCRYPTION/DECRYPTION ERRORS ========= */
        /**
         * Xử lý exception khi encryption thất bại
         * 
         * @param ex EncryptionException
         * @return ResponseEntity với status 400 (bad request) vì thường do input
         *         invalid
         */
        @ExceptionHandler(EncryptionException.class)
        public ResponseEntity<ApiRes<Void>> handleEncryptionException(EncryptionException ex) {
                String errorCode = ex.getErrorCodeValue();
                logger.error("HANDLER_ENC_ERROR [{}]: {} | cause: {}",
                                errorCode,
                                ex.getMessage(),
                                ex.getCause() != null ? ex.getCause().getMessage() : "none",
                                ex);

                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                                .body(ApiRes.error(
                                                String.format("%s [%s]", ex.getMessage(), errorCode),
                                                HttpStatus.BAD_REQUEST.value()));
        }

        /**
         * Xử lý exception khi decryption thất bại
         * 
         * @param ex DecryptionException
         * @return ResponseEntity với status 400 (bad request) nếu format invalid, 500
         *         nếu error khác
         */
        @ExceptionHandler(DecryptionException.class)
        public ResponseEntity<ApiRes<Void>> handleDecryptionException(DecryptionException ex) {
                String errorCode = ex.getErrorCodeValue();

                logger.error("HANDLER_DECRYPTION_ERROR [{}]: {} | timestamp: {}",
                                errorCode,
                                ex.getMessage(),
                                ex.getTimestamp());

                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                                .body(ApiRes.error("Invalid encrypted data", HttpStatus.BAD_REQUEST.value()));
        }

        /**
         * Xử lý exception khi encrypted data format không hợp lệ
         * 
         * @param ex InvalidEncryptedDataException
         * @return ResponseEntity với status 400 (bad request)
         */
        @ExceptionHandler(InvalidEncryptedDataException.class)
        public ResponseEntity<ApiRes<Void>> handleInvalidEncryptedDataException(InvalidEncryptedDataException ex) {
                String errorCode = ex.getErrorCodeValue();

                logger.error("HANDLER_INVALID_ENCRYPTED_DATA [{}]: {} | timestamp: {}",
                                errorCode,
                                ex.getMessage(),
                                ex.getTimestamp());

                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                                .body(ApiRes.error("Invalid encrypted data format", HttpStatus.BAD_REQUEST.value()));
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
                                .forEach(error -> {
                                        errors.put(error.getField(), error.getDefaultMessage());
                                        logger.debug("VALIDATION_ERROR: field={}, message={}",
                                                        error.getField(),
                                                        error.getDefaultMessage());
                                });

                logger.warn("HANDLER_VALIDATION_FAILED: {} field error(s) - details: {}",
                                errors.size(),
                                errors);

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

                ex.getConstraintViolations()
                                .forEach(violation -> {
                                        String field = violation.getPropertyPath().toString();
                                        String message = violation.getMessage();
                                        errors.put(field, message);

                                        logger.debug("CONSTRAINT_VIOLATION: field={}, message={}",
                                                        field,
                                                        message);
                                });

                logger.warn("HANDLER_CONSTRAINT_VIOLATION: {} constraint error(s) - details: {}",
                                errors.size(),
                                errors);

                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                                .body(ApiRes.<Map<String, String>>builder()
                                                .success(false)
                                                .status(400)
                                                .message(AppConstants.VALIDATION_FAILED)
                                                .data(errors)
                                                .timestamp(java.time.LocalDateTime.now())
                                                .build());
        }

        /* ========= TRANSFER ERRORS ========= */
        @ExceptionHandler(TransferException.class)
        public ResponseEntity<ApiRes<Void>> handleTransferException(TransferException ex) {
                String errorCode = ex.getErrorCodeValue();
                HttpStatus status = determineTransferErrorStatus(errorCode);

                logger.error("HANDLER_TRANSFER_ERROR [{}]: {} | timestamp: {}",
                                errorCode,
                                ex.getMessage(),
                                ex.getTimestamp());

                return ResponseEntity.status(status)
                                .body(ApiRes.error(ex.getMessage(), status.value()));
        }

        private HttpStatus determineTransferErrorStatus(String errorCode) {
                if (errorCode.contains("VALIDATION") || errorCode.contains("TRF_00[1-5]")) {
                        return HttpStatus.BAD_REQUEST;
                }
                return HttpStatus.INTERNAL_SERVER_ERROR;
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
                logger.error("HANDLER_UNEXPECTED_ERROR: {} | message: {} | stacktrace: ",
                                ex.getClass().getSimpleName(),
                                ex.getMessage(),
                                ex);

                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                .body(ApiRes.error(
                                                AppConstants.INTERNAL_SERVER_ERROR,
                                                500));
        }
}