package com.banking.transactionservice.client;

import com.banking.transactionservice.dto.shared.ApiResponse;
import jakarta.validation.Valid;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

import java.math.BigDecimal;


@FeignClient(name = "account-service", url = "${account.service.url}")
public interface AccountServiceClient {

    @PatchMapping("/api/v1/accounts/{accountNumber}/debit")
    ApiResponse<BigDecimal> debitBalance(
            @PathVariable(name = "accountNumber") Long accountNumber,
            @RequestBody @Valid BigDecimal amount
    );
}
