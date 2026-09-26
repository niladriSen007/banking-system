package com.banking.postservice.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

	@ExceptionHandler(PostNotFoundException.class)
	public ResponseEntity<?> handlePostNotFound(
			PostNotFoundException exception
	) {

		return ResponseEntity
				.status(HttpStatus.NOT_FOUND)
				.body(
						Map.of(
								"timestamp",
								Instant.now(),

								"status",
								404,

								"error",
								"POST_NOT_FOUND",

								"message",
								exception.getMessage()
						)
				);
	}
}