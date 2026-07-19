package com.banking.accountservice.controller;

import com.banking.accountservice.dto.request.AccountRequest;
import com.banking.accountservice.dto.request.UpdateAccountRequest;
import com.banking.accountservice.dto.response.AccountResponse;
import com.banking.accountservice.dto.shared.ApiResponse;
import com.banking.accountservice.service.IAccountService;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/accounts")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class AccountController {
    IAccountService accountService;

    @PostMapping
    public ResponseEntity<ApiResponse<AccountResponse>> createAccount(@RequestBody @Valid AccountRequest accountRequest) {
        log.info("Creating account - Controller");
        return ResponseEntity.ok(
                ApiResponse.success(accountService.createAccount(accountRequest), HttpStatus.CREATED.value())
        );
    }

    @GetMapping
    public ResponseEntity<ApiResponse<AccountResponse>> getAccount(@RequestParam(name = "accountNumber") @Valid Long accountNumber) {
        return ResponseEntity.ok(
                ApiResponse.success(
                        accountService.getAccountDetails(accountNumber),
                        HttpStatus.OK.value()
                )
        );
    }

    @PatchMapping("/{accountNumber}")
    public ResponseEntity<ApiResponse<AccountResponse>> updateAccount(@RequestBody @Valid UpdateAccountRequest updateAccountRequest,
                                                                      @PathVariable(name = "accountNumber") Long accountNumber
    ) {
        return ResponseEntity.ok(
                ApiResponse.success(
                        accountService.updateAccountDetails(accountNumber, updateAccountRequest),
                        HttpStatus.OK.value()
                )
        );
    }

    @DeleteMapping("/{accountNumber}")
    public ResponseEntity<ApiResponse<Void>> deleteAccount(@RequestParam(name = "accountNumber") Long accountNumber) {
        return ResponseEntity.ok(
                ApiResponse.success(
                        accountService.deleteAccount(accountNumber),
                        HttpStatus.OK.value()
                )
        );
    }

    @GetMapping("/{accountNumber}/balance")
    public ResponseEntity<ApiResponse<BigDecimal>> getAccountBalance(@PathVariable(name = "accountNumber") Long accountNumber) {
        return ResponseEntity.ok(
                ApiResponse.success(
                        accountService.getAccountBalance(accountNumber),
                        HttpStatus.OK.value()
                )
        );
    }

    @PatchMapping("/{accountNumber}/block-account")
    public ResponseEntity<ApiResponse<Boolean>> blockAccount(@PathVariable(name = "accountNumber") Long accountNumber) {
        return ResponseEntity.ok(
                ApiResponse.success(
                        accountService.blockAccount(accountNumber),
                        HttpStatus.OK.value()
                )
        );
    }


    // SAGA Step starts here

    /**
     * This is called by Transaction service when transfer is initiated
     */
    @PatchMapping("/{accountNumber}/debit")
    public ResponseEntity<ApiResponse<BigDecimal>> deductBalance(
            @PathVariable(name = "accountNumber") Long accountNumber,
            @RequestBody @Valid BigDecimal amount
    ) {
        return ResponseEntity.ok(
                ApiResponse.success(
                        accountService.deductBalance(accountNumber, amount),
                        HttpStatus.OK.value()
                )
        );
    }

    /**
     * Called by Transaction service in TWO Scenarios
     * 1. Credit Balance -> After Transaction completes
     * 2. Refund Balance -> After Fraud Detection
     */
    @PatchMapping("/{accountNumber}/credit")
    public ResponseEntity<ApiResponse<BigDecimal>> creditBalance(
            @PathVariable(name = "accountNumber") Long accountNumber,
            @RequestBody @Valid BigDecimal amount
    ) {
        return ResponseEntity.ok(
                ApiResponse.success(
                        accountService.creditBalnce(accountNumber, amount),
                        HttpStatus.OK.value()
                )
        );
    }

}
