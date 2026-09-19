package com.banking.accountservice.dto.response;

import com.banking.accountservice.entity.AccountStatus;
import com.banking.accountservice.entity.AccountType;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record AccountResponse(
        Long id,
        String accountHolderName,
        String accountNumber,
        String email,
        String phoneNumber,
        AccountType accountType,
        AccountStatus accountStatus,
        BigDecimal balance,
        BigDecimal dailyTransactionLimit,
        LocalDateTime createdAt
) {
}

