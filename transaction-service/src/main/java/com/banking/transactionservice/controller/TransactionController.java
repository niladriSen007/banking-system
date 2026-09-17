package com.banking.transactionservice.controller;


import com.banking.transactionservice.dto.request.TransactionRequest;
import com.banking.transactionservice.dto.response.TransactionResponse;
import com.banking.transactionservice.dto.shared.ApiResponse;
import com.banking.transactionservice.service.ITransactionService;
import io.swagger.v3.oas.annotations.Operation;
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

    /**
     * This is the endpoint to transfer the amount
     * @param transactionRequest
     * @return
     */
    @PostMapping("/transfer")
    @Operation(
            description = "Transfer amount controller",
            responses = {
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", ref = "successfulResponse"),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", ref = "badRequest"),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", ref = "internalServerError")
            }
    )
    public ResponseEntity<ApiResponse<TransactionResponse>> transferAmount(@RequestBody @Valid TransactionRequest transactionRequest) {
        log.info("Transfer amount - Controller");
        return ResponseEntity.ok(
                ApiResponse.success(transactionService.transferAmount(transactionRequest), HttpStatus.CREATED.value())
        );
    }

    @GetMapping("/{transactionId}")
    public ResponseEntity<ApiResponse<TransactionResponse>> getTransactionInfo(@PathVariable(name = "transactionId") @Valid String transactionId) {
        log.info("getTransactionInfo - Controller");
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
        log.info("getTransactionHistory - Controller");
        return ResponseEntity.ok(
                ApiResponse.success(
                        transactionService.getTransactionHistory(accountNumber),
                        HttpStatus.OK.value()
                )
        );
    }

    @PostMapping("/{transactionReferenceNumber}/verify")
    public ResponseEntity<ApiResponse<TransactionResponse>> verifyOtp(
            @PathVariable(name = "transactionReferenceNumber") String transactionReferenceNumber,
            @RequestParam("otp") @Valid String otp
    ) {
        log.info("verifyOtp - Controller");
        return ResponseEntity.ok(
                ApiResponse.success(
                        transactionService.verifyOtp(transactionReferenceNumber, otp),
                        HttpStatus.OK.value()
                )
        );
    }

    @GetMapping("/count")
    public ResponseEntity<ApiResponse<Long>> getTransactionCount(
            @RequestParam(name = "accountNumber") @Valid Long accountNumber
    ) {
        log.info("getTransactionCount - Controller");
        return ResponseEntity.ok(
                ApiResponse.success(
                        transactionService.getTotalTransactionCount(accountNumber),
                        HttpStatus.OK.value()
                )
        );
    }
}
