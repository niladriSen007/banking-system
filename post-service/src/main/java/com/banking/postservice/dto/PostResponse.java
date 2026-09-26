package com.banking.postservice.dto;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.UUID;

public record PostResponse(

		UUID postId,

		UUID userId,

		String content,

		LocalDateTime createdAt,

		LocalDateTime updatedAt,

		Long likeCount,

		Long commentCount
) {
}