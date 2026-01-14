package com.example.crudjob.exception;

import java.time.LocalDateTime;

import com.example.crudjob.entity.enums.ErrorCode;

/**
 * Exception thrown when transfer operation fails
 */
public class TransferException extends RuntimeException {

    private ErrorCode errorCode;
    private LocalDateTime timestamp;

    public TransferException(String message) {
        super(message);
        this.timestamp = LocalDateTime.now();
    }

    public TransferException(String message, Throwable cause) {
        super(message, cause);
        this.timestamp = LocalDateTime.now();
    }

    public TransferException(ErrorCode errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
        this.timestamp = LocalDateTime.now();
    }

    public TransferException(ErrorCode errorCode, String message, Throwable cause) {
        super(message, cause);
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
