package com.example.crudjob.exception;

import java.time.LocalDateTime;

import com.example.crudjob.entity.enums.ErrorCode;

/**
 * Exception thrown when encryption operation fails
 */
public class EncryptionException extends RuntimeException {
    
    private ErrorCode errorCode;
    private LocalDateTime timestamp;
    
    public EncryptionException(String message) {
        super(message);
        this.timestamp = LocalDateTime.now();
    }
    
    public EncryptionException(String message, Throwable cause) {
        super(message, cause);
        this.timestamp = LocalDateTime.now();
    }
    
    public EncryptionException(ErrorCode errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
        this.timestamp = LocalDateTime.now();
    }
    
    public ErrorCode getErrorCode() {
        return errorCode;
    }
    
    public String getErrorCodeValue() {
        return errorCode != null ? errorCode.getCode() : "UNKNOWN";
    }
    
    public LocalDateTime getTimestamp() {
        return timestamp;
    }
}