package com.banking.transactionservice.dto.shared;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Builder
@Data
public class ApiResponse<T> {
    private String status;
    private int statusCode;
    private String message;
    private T data;
    private LocalDateTime timestamp;
    private String error;

    public static <T> ApiResponse<T> success(T data, int statusCode) {
        return ApiResponse.<T>builder()
                .status("success")
                .statusCode(statusCode)
                .data(data)
                .error(null)
                .timestamp(LocalDateTime.now())
                .build();
    }

    public static <T> ApiResponse<T> success(T data) {
        return success(data, 200);
    }

    public static <T> ApiResponse<T> error(String errorMessage, int statusCode) {
        return ApiResponse.<T>builder()
                .status("error")
                .statusCode(statusCode)
                .data(null)
                .error(errorMessage)
                .timestamp(LocalDateTime.now())
                .build();
    }

}

