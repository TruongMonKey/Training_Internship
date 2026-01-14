package com.example.crudjob.dto.request;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * PlainTransferRequest
 *
 * DÙNG CHO DEV / TEST
 * PLAINTEXT input từ Postman
 */
@Data
public class PlainTransferRequest {

    @NotBlank
    private String transactionId;

    @NotBlank
    private String sourceAccount;

    @NotBlank
    private String targetAccount;

    @NotNull
    private BigDecimal inDebt;

    @NotNull
    private BigDecimal have;

    @NotNull
    private LocalDateTime time;
}
