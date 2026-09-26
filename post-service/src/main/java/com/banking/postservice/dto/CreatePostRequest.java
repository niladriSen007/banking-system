package com.banking.postservice.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record CreatePostRequest(

		@NotBlank
		String content

) {
}