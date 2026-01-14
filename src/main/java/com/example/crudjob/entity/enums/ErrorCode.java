package com.example.crudjob.entity.enums;

/**
 * Enum định nghĩa tất cả error code - không hardcode
 */
public enum ErrorCode {
    // Encryption errors
    ENC_PLAINTEXT_NULL("ENC_001", "Plaintext cannot be null"),
    ENC_PLAINTEXT_BLANK("ENC_002", "Plaintext cannot be blank"),
    ENC_AES_KEY_FAILED("ENC_003", "Failed to generate AES key"),
    ENC_AES_ENCRYPT_FAILED("ENC_004", "AES encryption failed"),
    ENC_AES_ENCRYPT_NULL("ENC_005", "AES encryption returned null payload"),
    ENC_RSA_ENCRYPT_FAILED("ENC_006", "RSA encryption failed"),
    ENC_RSA_ENCRYPT_NULL("ENC_007", "RSA encryption returned null or blank result"),
    ENC_COMBINE_FAILED("ENC_008", "Failed to combine encrypted parts"),
    ENC_RESULT_NULL("ENC_009", "Combined encrypted data is null or blank"),

    // Decryption errors
    DEC_INPUT_NULL("DEC_001", "Cannot decrypt null value"),
    DEC_INPUT_BLANK("DEC_002", "Cannot decrypt blank value"),
    DEC_PARSE_FAILED("DEC_003", "Failed to parse encrypted data"),
    DEC_PARTS_COUNT_INVALID("DEC_004", "Invalid encrypted format - wrong number of parts"),
    DEC_ENCRYPTED_DATA_BLANK("DEC_005", "Encrypted data part is blank"),
    DEC_IV_BLANK("DEC_006", "IV part is blank"),
    DEC_AES_KEY_BLANK("DEC_007", "Encrypted AES key part is blank"),
    DEC_RSA_DECRYPT_FAILED("DEC_008", "RSA decryption failed"),
    DEC_RSA_DECRYPT_NULL("DEC_009", "RSA decryption returned null or empty key"),
    DEC_AES_KEY_RESTORE_FAILED("DEC_010", "Failed to restore AES key"),
    DEC_AES_KEY_RESTORE_NULL("DEC_011", "Failed to restore AES key - returned null"),
    DEC_AES_DECRYPT_FAILED("DEC_012", "AES decryption failed"),
    DEC_AES_DECRYPT_NULL("DEC_013", "AES decryption returned null or empty data"),

    // Invalid format errors
    INV_FORMAT_PARTS_COUNT("INV_001", "Invalid encrypted data format"),

    // Transfer errors
    TRANSFER_VALIDATION_FAILED("TRF_001", "Transfer validation failed"),
    TRANSFER_SOURCE_ACCOUNT_NULL("TRF_002", "Source account cannot be null or empty"),
    TRANSFER_TARGET_ACCOUNT_NULL("TRF_003", "Target account cannot be null or empty"),
    TRANSFER_SAME_ACCOUNT("TRF_004", "Source and target accounts cannot be the same"),
    TRANSFER_INVALID_AMOUNT("TRF_005", "Transfer amount must be positive"),
    TRANSFER_PERSISTENCE_FAILED("TRF_006", "Failed to save transaction to database"),
    TRANSFER_DECRYPTION_FAILED("TRF_007", "Failed to decrypt transfer parameters");

    private final String code;
    private final String defaultMessage;

    ErrorCode(String code, String defaultMessage) {
        this.code = code;
        this.defaultMessage = defaultMessage;
    }

    public String getCode() {
        return code;
    }

    public String getDefaultMessage() {
        return defaultMessage;
    }
}