package com.example.crudjob.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * EncryptedTransferCommand
 *
 * DTO dùng cho giao tiếp GIỮA CÁC SERVICE.
 *
 * QUY ƯỚC BẮT BUỘC:
 * - TẤT CẢ field đều đã được mã hóa RSA
 * - KHÔNG BAO GIỜ chứa plaintext
 * - Là contract chính thức giữa các service
 */
@Data
public class EncryptedTransferCommand {

    /** RSA encrypted Transaction ID */
    @NotBlank
    private String transactionId;

    /** RSA encrypted source account */
    @NotBlank
    private String sourceAccount;

    /** RSA encrypted target account */
    @NotBlank
    private String targetAccount;

    /** RSA encrypted BigDecimal (InDebt) */
    @NotBlank
    private String encryptedInDebt;

    /** RSA encrypted BigDecimal (Have) */
    @NotBlank
    private String encryptedHave;

    /** RSA encrypted LocalDateTime (ISO-8601) */
    @NotBlank
    private String time;
}
