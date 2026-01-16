package com.example.crudjob.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.example.crudjob.service.AesAttributeConverter;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

/**
 * Entity lưu lịch sử giao dịch.
 *
 * Mỗi bản ghi tương ứng với:
 * - Một giao dịch NỢ hoặc CÓ
 * - Một TransactionID nghiệp vụ
 *
 * Lưu ý:
 * - Số tài khoản (account) được mã hóa AES trước khi lưu DB
 * - Validate ở mức dữ liệu, không chứa logic nghiệp vụ
 */
@Entity
@Table(name = "transaction_history", indexes = {
        @Index(name = "idx_transaction_id", columnList = "transactionId"),
        @Index(name = "idx_transaction_time", columnList = "transactionTime")
})
@Getter
@Setter
public class TransactionHistory {

    /**
     * ID nội bộ của bản ghi (auto-increment).
     * Chỉ dùng cho DB, không dùng cho nghiệp vụ.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Mã giao dịch nghiệp vụ.
     * Không được null hoặc rỗng.
     */
    @NotBlank(message = "TransactionId must not be blank")
    @Column(name = "transaction_id", nullable = false, length = 100)
    private String transactionId;

    /**
     * Số tài khoản (đã được mã hóa AES).
     * Không được null hoặc rỗng.
     */
    @NotBlank(message = "Account must not be blank")
    @Column(name = "account", nullable = false, length = 512)
    @Convert(converter = AesAttributeConverter.class)
    private String account;

    /**
     * Số tiền ghi nợ.
     * Phải >= 0.
     */
    @NotNull(message = "InDebt must not be null")
    @DecimalMin(value = "0.0", inclusive = true, message = "InDebt must be >= 0")
    @Column(name = "in_debt", precision = 18, scale = 2)
    private BigDecimal inDebt;

    /**
     * Số tiền ghi có.
     * Phải >= 0.
     */
    @NotNull(message = "Have must not be null")
    @DecimalMin(value = "0.0", inclusive = true, message = "Have must be >= 0")
    @Column(name = "have", precision = 18, scale = 2)
    private BigDecimal have;

    /**
     * Thời gian phát sinh giao dịch.
     * Không được null.
     */
    @NotNull(message = "TransactionTime must not be null")
    @Column(name = "transaction_time", nullable = false)
    private LocalDateTime transactionTime;
}
