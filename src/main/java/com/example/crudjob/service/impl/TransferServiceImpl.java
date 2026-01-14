package com.example.crudjob.service.impl;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.crudjob.dto.EncryptedTransferCommand;
import com.example.crudjob.entity.TransactionHistory;
import com.example.crudjob.entity.enums.ErrorCode;
import com.example.crudjob.exception.DecryptionException;
import com.example.crudjob.exception.EncryptionException;
import com.example.crudjob.exception.InvalidEncryptedDataException;
import com.example.crudjob.exception.TransferException;
import com.example.crudjob.repository.TransactionHistoryRepository;
import com.example.crudjob.service.EncryptionService;
import com.example.crudjob.service.TransferService;
import com.example.crudjob.utils.SecureLogUtil;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * TransferServiceImpl
 *
 * Trách nhiệm:
 * - Giải mã RSA dữ liệu nhận từ service khác
 * - Validate nghiệp vụ
 * - Lưu lịch sử giao dịch (Account = AES)
 * - Log đầy đủ, CHE TOÀN BỘ dữ liệu nhạy cảm
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class TransferServiceImpl implements TransferService {

        private final TransactionHistoryRepository repository;
        private final EncryptionService encryptionService;

        /**
         * =========================
         * TRANSFER BUSINESS API
         * =========================
         */
        @Override
        @Transactional
        public void transfer(EncryptedTransferCommand cmd) {

                String transactionId = null;

                try {
                        /* ===== 1. DECRYPT DATA IN TRANSIT (RSA) ===== */
                        transactionId = encryptionService.decrypt(cmd.getTransactionId());
                        String sourceAccount = encryptionService.decrypt(cmd.getSourceAccount());
                        String targetAccount = encryptionService.decrypt(cmd.getTargetAccount());

                        BigDecimal inDebt = new BigDecimal(
                                        encryptionService.decrypt(cmd.getEncryptedInDebt()));

                        BigDecimal have = new BigDecimal(
                                        encryptionService.decrypt(cmd.getEncryptedHave()));

                        LocalDateTime time = LocalDateTime.parse(
                                        encryptionService.decrypt(cmd.getTime()));

                        /* ===== 2. LOG START (MASKED) ===== */
                        log.info(
                                        "TRANSFER_START | {}",
                                        SecureLogUtil.mask(
                                                        String.format(
                                                                        "transactionId=%s | sourceAccount=%s | targetAccount=%s | inDebt=%s | have=%s | time=%s",
                                                                        transactionId, sourceAccount, targetAccount,
                                                                        inDebt, have, time)));

                        /* ===== 3. VALIDATION ===== */
                        validate(sourceAccount, targetAccount, inDebt, have);

                        /* ===== 4. SAVE DEBIT ===== */
                        save(transactionId, sourceAccount, inDebt, BigDecimal.ZERO, time);

                        /* ===== 5. SAVE CREDIT ===== */
                        save(transactionId, targetAccount, BigDecimal.ZERO, have, time);

                        log.info("TRANSFER_SUCCESS | transactionId=?");

                }
                /* ===== FORMAT / RSA ERRORS ===== */
                catch (InvalidEncryptedDataException e) {
                        log.error(
                                        "TRANSFER_FAILED | {} | {}",
                                        SecureLogUtil.mask(
                                                        String.format("transactionId=%s | errorCode=%s",
                                                                        transactionId, e.getErrorCodeValue())),
                                        e.getClass().getSimpleName());
                        throw e;
                } catch (DecryptionException e) {
                        log.error(
                                        "TRANSFER_FAILED | {} | {}",
                                        SecureLogUtil.mask(
                                                        String.format("transactionId=%s | errorCode=%s",
                                                                        transactionId, e.getErrorCodeValue())),
                                        e.getClass().getSimpleName());
                        throw e;
                }
                /* ===== VALIDATION ERRORS ===== */
                catch (IllegalArgumentException e) {
                        log.error(
                                        "TRANSFER_FAILED | {}",
                                        SecureLogUtil.mask(
                                                        String.format(
                                                                        "transactionId=%s | validationError=%s",
                                                                        transactionId, e.getMessage())));
                        throw new TransferException(
                                        ErrorCode.TRANSFER_VALIDATION_FAILED,
                                        "Transfer validation failed",
                                        e);
                }
                /* ===== DB / ENCRYPTION AT REST ERRORS ===== */
                catch (TransferException e) {
                        // Đã log ở tầng persistence
                        throw e;
                }
        }

        /**
         * =========================
         * ENCRYPT COMMAND FOR OTHER SERVICES
         * =========================
         */
        @Override
        public EncryptedTransferCommand encryptTransferCommand(
                        String transactionId,
                        String sourceAccount,
                        String targetAccount,
                        BigDecimal inDebt,
                        BigDecimal have,
                        LocalDateTime time) {

                log.debug("TRANSFER_RSA_ENCRYPT | input=????");

                EncryptedTransferCommand cmd = new EncryptedTransferCommand();
                cmd.setTransactionId(encryptionService.encrypt(transactionId));
                cmd.setSourceAccount(encryptionService.encrypt(sourceAccount));
                cmd.setTargetAccount(encryptionService.encrypt(targetAccount));
                cmd.setEncryptedInDebt(encryptionService.encrypt(inDebt.toPlainString()));
                cmd.setEncryptedHave(encryptionService.encrypt(have.toPlainString()));
                cmd.setTime(encryptionService.encrypt(time.toString()));

                return cmd;
        }

        /*
         * =========================
         * INTERNAL HELPERS
         * =========================
         */

        private void validate(
                        String sourceAccount,
                        String targetAccount,
                        BigDecimal inDebt,
                        BigDecimal have) {

                if (sourceAccount.equals(targetAccount)) {
                        throw new IllegalArgumentException(
                                        ErrorCode.TRANSFER_SAME_ACCOUNT.getDefaultMessage());
                }

                if (inDebt.signum() <= 0 && have.signum() <= 0) {
                        throw new IllegalArgumentException(
                                        ErrorCode.TRANSFER_INVALID_AMOUNT.getDefaultMessage());
                }
        }

        private void save(
                        String transactionId,
                        String plainAccount,
                        BigDecimal inDebt,
                        BigDecimal have,
                        LocalDateTime time) {

                try {
                        TransactionHistory h = new TransactionHistory();
                        h.setTransactionId(transactionId);
                        h.setAccount(encryptionService.encrypt(plainAccount)); // AES at rest
                        h.setInDebt(inDebt);
                        h.setHave(have);
                        h.setTransactionTime(time);

                        repository.save(h);

                } catch (EncryptionException e) {
                        log.error("PERSIST_FAILED | {}", SecureLogUtil.mask(String.format(
                                        "transactionId=%s | account=%s | inDebt=%s | have=%s | time=%s",
                                        transactionId, plainAccount, inDebt, have,
                                        time)), e);
                        throw new TransferException(
                                        ErrorCode.TRANSFER_PERSISTENCE_FAILED,
                                        "Failed to encrypt account for persistence",
                                        e);
                }
        }
}
