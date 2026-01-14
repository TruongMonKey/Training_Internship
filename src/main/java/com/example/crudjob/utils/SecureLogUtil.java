package com.example.crudjob.utils;

import java.util.regex.Pattern;

public class SecureLogUtil {

    // Pattern để che giấu giá trị của các field nhạy cảm

    // Transaction ID
    private static final Pattern TRANSACTION_ID_PATTERN = Pattern.compile(
            "(?i)\\b(transactionId|transaction_id)\\b=([^\\s,|]+)");

    // Account (số tài khoản nguồn / đích)
    private static final Pattern ACCOUNT_PATTERN = Pattern.compile(
            "(?i)\\b(account|sourceAccount|targetAccount|source_account|target_account)\\b=([^\\s,|]+)");

    // Amount (nợ / có) - che bằng 1 dấu ?
    private static final Pattern AMOUNT_PATTERN = Pattern.compile(
            "(?i)\\b(inDebt|have|amount|in_debt)\\b=([^\\s,|]+)");

    // Thời gian phát sinh giao dịch
    private static final Pattern TIME_PATTERN = Pattern.compile(
            "(?i)\\b(transactionTime|transaction_time|time)\\b=([^\\s,|]+)");

    /**
     * Che giấu toàn bộ thông tin nhạy cảm trong log message
     * 
     * @param message Log message gốc
     * @return Message đã che giấu thông tin nhạy cảm
     */
    public static String mask(String message) {
        if (message == null || message.isBlank()) {
            return message;
        }

        String masked = message;

        // Che giấu Transaction ID
        masked = TRANSACTION_ID_PATTERN.matcher(masked).replaceAll("$1=?");

        // Che giấu Account (số tài khoản)
        masked = ACCOUNT_PATTERN.matcher(masked).replaceAll("$1=?");

        // Che giấu Amount (nợ, có)
        masked = AMOUNT_PATTERN.matcher(masked).replaceAll("$1=?");

        // Che giấu Time
        masked = TIME_PATTERN.matcher(masked).replaceAll("$1=?");

        return masked;
    }

    /**
     * Che giấu thông tin nhạy cảm trong exception message
     * 
     * @param exception Exception cần xử lý
     * @return Exception message đã che giấu
     */
    public static String maskException(Throwable exception) {
        if (exception == null || exception.getMessage() == null) {
            return "";
        }
        return mask(exception.getMessage());
    }
}
