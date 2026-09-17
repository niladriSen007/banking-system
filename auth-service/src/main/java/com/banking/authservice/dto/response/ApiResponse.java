package com.banking.authservice.dto.response;

import lombok.Builder;
import lombok.Getter;

import java.time.Instant;

@Getter
@Builder
public class ApiResponse<T> {

	private String status;
	private int statusCode;
	private T data;
	private String error;
	private Instant timestamp;

	public static <T> ApiResponse<T> success(T data, int statusCode) {
		return ApiResponse.<T>builder()
				.status("success")
				.statusCode(statusCode)
				.data(data)
				.error(null)
				.timestamp(Instant.now())
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
				.timestamp(Instant.now())
				.build();
	}
}
