package com.example.crudjob.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.example.crudjob.dto.EncryptedTransferCommand;

/**
 * TransferService
 *
 * Nguyên tắc:
 * - Service boundary CHỈ nhận dữ liệu đã RSA encrypt
 * - Không cho phép plaintext đi qua boundary
 */
public interface TransferService {

    /**
     * Thực hiện chuyển khoản.
     *
     * @param command EncryptedTransferCommand (RSA encrypted)
     */
    void transfer(EncryptedTransferCommand command);

    /**
     * Hỗ trợ service khác:
     * - Nhận plaintext nội bộ
     * - Trả về EncryptedTransferCommand chuẩn RSA
     *
     * @return encrypted command dùng để truyền service-to-service
     */
    EncryptedTransferCommand encryptTransferCommand(
            String transactionId,
            String sourceAccount,
            String targetAccount,
            BigDecimal inDebt,
            BigDecimal have,
            LocalDateTime time);
}
