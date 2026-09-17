package com.banking.authservice.exceptions;


import io.jsonwebtoken.JwtException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

	@ExceptionHandler(CannotRegisterAsAdminException.class)
	public ResponseEntity<ApiResponse<Void>> cannotRegisterAsAdminException(CannotRegisterAsAdminException e) {
		return ResponseEntity.status(HttpStatus.FORBIDDEN)
				.body(ApiResponse.error(e.getMessage(), HttpStatus.FORBIDDEN.value()));
	}

	@ExceptionHandler(UserAlreadyExistsException.class)
	public ResponseEntity<ApiResponse<Void>> userAlreadyExistsException(UserAlreadyExistsException e) {
		return ResponseEntity.status(HttpStatus.CONFLICT)
				.body(ApiResponse.error(e.getMessage(), HttpStatus.CONFLICT.value()));
	}

	@ExceptionHandler(InvalidCredentialsException.class)
	public ResponseEntity<ApiResponse<Void>> invalidCredentialsException(InvalidCredentialsException e) {
		return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
				.body(ApiResponse.error(e.getMessage(), HttpStatus.UNAUTHORIZED.value()));
	}

	@ExceptionHandler(AuthenticationException.class)
	public ResponseEntity<ApiResponse<Void>> handleAuthenticationException(AuthenticationException e) {
		return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
				.body(ApiResponse.error("Authentication failed: " + e.getMessage(), HttpStatus.UNAUTHORIZED.value()));
	}

	@ExceptionHandler(AccessDeniedException.class)
	public ResponseEntity<ApiResponse<Void>> handleAccessDeniedException(AccessDeniedException e) {
		return ResponseEntity.status(HttpStatus.FORBIDDEN)
				.body(ApiResponse.error("Access denied: " + e.getMessage(), HttpStatus.FORBIDDEN.value()));
	}

	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<ApiResponse<Void>> handleValidationException(MethodArgumentNotValidException e) {
		String errors = e.getBindingResult().getFieldErrors().stream()
				.map(fe -> fe.getField() + ": " + fe.getDefaultMessage())
				.collect(Collectors.joining(", "));
		return ResponseEntity.status(HttpStatus.BAD_REQUEST)
				.body(ApiResponse.error(errors, HttpStatus.BAD_REQUEST.value()));
	}

	@ExceptionHandler(JwtException.class)
	public ResponseEntity<ApiResponse<Void>> handleJwtException(JwtException e) {
		return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
				.body(ApiResponse.error(e.getMessage(), HttpStatus.UNAUTHORIZED.value()));
	}

	@ExceptionHandler(Exception.class)
	public ResponseEntity<ApiResponse<Void>> handleGenericException(Exception e) {
		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
				.body(ApiResponse.error("An unexpected error occurred: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR.value()));
	}
}