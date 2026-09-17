package com.banking.authservice.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class RefreshResponse {
	private String title;
	private String message;
	private String accessToken;
	private String refreshToken;
}
