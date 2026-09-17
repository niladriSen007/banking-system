package com.banking.authservice.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class LoginResponse {
	private String accessToken;
	private String refreshToken;
	private String title;
	private String message;
	private UserResponse userResponse;
}