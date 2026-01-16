package com.example.crudjob.dto.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import lombok.Data;

@Data
public class DecryptedTransferResponse {

    private String transactionId;
    private String sourceAccount;
    private String targetAccount;
    private BigDecimal inDebt;
    private BigDecimal have;
    private LocalDateTime time;
}
