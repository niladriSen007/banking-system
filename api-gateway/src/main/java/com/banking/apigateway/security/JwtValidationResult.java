package com.banking.apigateway.security;

import java.util.Set;

public record JwtValidationResult(
		boolean valid,
		String userId,
		String username,
		String role,
		Set<String> authorities
) {

	public static JwtValidationResult invalid() {
		return new JwtValidationResult(
				false,
				null,
				null,
				null,
				Set.of()
		);
	}

	public static JwtValidationResult valid(
			String userId,
			String username,
			String role,
			Set<String> authorities) {

		return new JwtValidationResult(
				true,
				userId,
				username,
				role,
				authorities
		);
	}
}