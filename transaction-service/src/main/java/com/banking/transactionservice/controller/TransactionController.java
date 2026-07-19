package com.banking.transactionservice.controller;


import com.banking.transactionservice.dto.request.TransactionRequest;
import com.banking.transactionservice.dto.response.TransactionResponse;
import com.banking.transactionservice.dto.shared.ApiResponse;
import com.banking.transactionservice.service.ITransactionService;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/transaction")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class TransactionController {

    ITransactionService transactionService;

    @PostMapping("/transfer")
    public ResponseEntity<ApiResponse<TransactionResponse>> transferAmount(@RequestBody @Valid TransactionRequest transactionRequest) {
        log.info("Creating account - Controller");
        return ResponseEntity.ok(
                ApiResponse.success(transactionService.transferAmount(transactionRequest), HttpStatus.CREATED.value())
        );
    }

    @GetMapping("/{transactionId}")
    public ResponseEntity<ApiResponse<TransactionResponse>> getTransactionInfo(@PathVariable(name = "transactionId") @Valid String transactionId) {
        return ResponseEntity.ok(
                ApiResponse.success(
                        transactionService.getTransactionInfo(transactionId),
                        HttpStatus.OK.value()
                )
        );
    }

    @GetMapping("/account/{accountNumber}")
    public ResponseEntity<ApiResponse<List<TransactionResponse>>> getTransactionHistory(
            @PathVariable(name = "accountNumber") Long accountNumber
    ) {
        return ResponseEntity.ok(
                ApiResponse.success(
                        transactionService.getTransactionHistory(accountNumber),
                        HttpStatus.OK.value()
                )
        );
    }

    @PostMapping("/{transactionId}/verify")
    public ResponseEntity<ApiResponse<TransactionResponse>> verifyOtp(@PathVariable(name = "transactionId") String transactionId,
                                                           @RequestParam("otp") @Valid String otp
    ) {
        return ResponseEntity.ok(
                ApiResponse.success(
                        transactionService.verifyOtp(transactionId, otp),
                        HttpStatus.OK.value()
                )
        );
    }
}
