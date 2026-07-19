package com.banking.accountservice.exceptions;

import com.banking.accountservice.dto.shared.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.support.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {



    @ExceptionHandler(AccountAlreadyExistsException.class)
    public ResponseEntity<ApiResponse<Void>> handleAccountAlreadyExistsException(AccountAlreadyExistsException e) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(ApiResponse.error(e.getMessage(), HttpStatus.FORBIDDEN.value()));
    }

    @ExceptionHandler(AccountDoesNotExistException.class)
    public ResponseEntity<ApiResponse<Void>> handleAccountDoesNotExistException(AccountDoesNotExistException e) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(ApiResponse.error(e.getMessage(), HttpStatus.FORBIDDEN.value()));
    }

    @ExceptionHandler(NotEnoughBalanceException.class)
    public ResponseEntity<ApiResponse<Void>> handleNotEnoughBalanceException(NotEnoughBalanceException e) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(ApiResponse.error(e.getMessage(), HttpStatus.FORBIDDEN.value()));
    }

    @ExceptionHandler(AccountNotActiveForTransactionsException.class)
    public ResponseEntity<ApiResponse<Void>> handleAccountNotActiveForTransactionsException(AccountNotActiveForTransactionsException e) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(ApiResponse.error(e.getMessage(), HttpStatus.FORBIDDEN.value()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Void>> handleValidationException(MethodArgumentNotValidException e) {
        String errors = e.getBindingResult().getFieldErrors().stream()
                .map(fe -> fe.getField() + ": " + fe.getDefaultMessage())
                .collect(Collectors.joining(", "));
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.error(errors, HttpStatus.BAD_REQUEST.value()));
    }


    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Void>> handleGenericException(Exception e) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error("An unexpected error occurred: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR.value()));
    }
}
