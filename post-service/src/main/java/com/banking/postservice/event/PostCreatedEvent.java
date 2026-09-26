package com.banking.postservice.event;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.UUID;

public record PostCreatedEvent(

		UUID eventId,

		UUID postId,

		UUID userId,

		String content,

		LocalDateTime createdAt
) {
}