package com.example.crudjob.controller;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import com.example.crudjob.dto.EncryptedTransferCommand;
import com.example.crudjob.dto.request.PlainTransferRequest;
import com.example.crudjob.service.TransferService;
import com.example.crudjob.utils.SecureLogUtil;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * TransferController
 *
 * Trách nhiệm:
 * - Nhận request
 * - Validate hình thức dữ liệu
 * - Log (MASKED)
 * - Delegate xuống Service
 *
 * KHÔNG:
 * - Xử lý nghiệp vụ
 * - Decrypt dữ liệu
 */
@RestController
@RequestMapping("/api/transfers")
@RequiredArgsConstructor
@Validated
@Slf4j
public class TransferController {

    private final TransferService transferService;

    /**
     * =================================================
     * API ENCRYPT (DEV / TEST ONLY)
     * =================================================
     *
     * Mục đích:
     * - Nhận PLAINTEXT
     * - Backend tự mã hoá RSA
     * - Trả về EncryptedTransferCommand
     *
     * Dùng để test Postman
     * KHÔNG dùng production
     */
    @PostMapping(value = "/encrypt", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<EncryptedTransferCommand> encryptForTest(
            @Valid @RequestBody PlainTransferRequest request) {

        log.info(
                "TRANSFER_ENCRYPT_TEST | {}",
                SecureLogUtil.mask(
                        String.format(
                                "transactionId=%s | sourceAccount=%s | targetAccount=%s | inDebt=%s | have=%s | time=%s",
                                request.getTransactionId(),
                                request.getSourceAccount(),
                                request.getTargetAccount(),
                                request.getInDebt(),
                                request.getHave(),
                                request.getTime())));

        EncryptedTransferCommand encrypted = transferService.encryptTransferCommand(
                request.getTransactionId(),
                request.getSourceAccount(),
                request.getTargetAccount(),
                request.getInDebt(),
                request.getHave(),
                request.getTime());

        return ResponseEntity.ok(encrypted);
    }

    /**
     * =================================================
     * API TRANSFER (PRODUCTION)
     * =================================================
     *
     * Input:
     * - EncryptedTransferCommand
     * - TẤT CẢ field đều RSA encrypted
     */
    @PostMapping
    public ResponseEntity<Void> transfer(
            @Valid @RequestBody EncryptedTransferCommand command) {

        log.info(
                "TRANSFER_REQUEST_RECEIVED | {}",
                SecureLogUtil.mask(
                        String.format(
                                "transactionId=%s | sourceAccount=%s | targetAccount=%s | inDebt=%s | have=%s | time=%s",
                                command.getTransactionId(),
                                command.getSourceAccount(),
                                command.getTargetAccount(),
                                command.getEncryptedInDebt(),
                                command.getEncryptedHave(),
                                command.getTime())));

        transferService.transfer(command);

        return ResponseEntity.ok().build();
    }
}
