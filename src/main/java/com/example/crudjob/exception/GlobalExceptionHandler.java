package com.example.crudjob.exception;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.example.crudjob.dto.response.ApiRes;
import com.example.crudjob.entity.constant.AppConstants;

@RestControllerAdvice
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
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                .body(ApiRes.error(
                                                AppConstants.INTERNAL_SERVER_ERROR,
                                                500));
        }
}