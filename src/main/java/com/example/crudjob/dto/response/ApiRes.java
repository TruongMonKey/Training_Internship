package com.example.crudjob.dto.response;

import java.time.LocalDateTime;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ApiRes<T> {

    private boolean success;
    private int status;
    private String message;
    private T data;
    private LocalDateTime timestamp;

    /*
     * =======================
     * FACTORY METHODS
     * =======================
     */

    public static <T> ApiRes<T> success(T data, String message, int status) {
        return ApiRes.<T>builder()
                .success(true)
                .status(status)
                .message(message)
                .data(data)
                .timestamp(LocalDateTime.now())
                .build();
    }

    public static <T> ApiRes<T> error(String message, int status) {
        return ApiRes.<T>builder()
                .success(false)
                .status(status)
                .message(message)
                .data(null)
                .timestamp(LocalDateTime.now())
                .build();
    }
}
