package com.example.crudjob.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Transfer Request DTO
 * 
 * Tất cả fields được RSA encrypt từ client
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TransferRequest {

    @NotBlank(message = "transactionId cannot be blank")
    private String transactionId;

    @NotBlank(message = "sourceAccount cannot be blank")
    private String sourceAccount;

    @NotBlank(message = "targetAccount cannot be blank")
    private String targetAccount;

    @NotBlank(message = "amount cannot be blank")
    private String amount;

    @NotBlank(message = "time cannot be blank")
    private String time;
}
