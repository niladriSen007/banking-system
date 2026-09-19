package com.banking.frauddetectionservice.client;

import com.banking.frauddetectionservice.dto.shared.ApiResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "transaction-service", url = "${transaction.service.url}")
public interface TransactionServiceClient {

    @GetMapping("/api/v1/transaction/count")
    ApiResponse<Long> getTransactionCount(@RequestParam(name = "accountNumber") String accountNumber);
}
