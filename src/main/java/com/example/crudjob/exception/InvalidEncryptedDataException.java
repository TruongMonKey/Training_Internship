package com.example.crudjob.exception;

/**
 * Exception thrown when encrypted data format is invalid
 */
public class InvalidEncryptedDataException extends BadRequestException {

    public InvalidEncryptedDataException(String message) {
        super(message);
    }

    public InvalidEncryptedDataException(String message, Throwable cause) {
        super(message, cause);
    }
}