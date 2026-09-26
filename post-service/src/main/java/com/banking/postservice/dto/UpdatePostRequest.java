package com.banking.postservice.dto;

import jakarta.validation.constraints.NotBlank;

public record UpdatePostRequest(

		@NotBlank
		String content,

		@NotBlank
		String visibility
) {
}