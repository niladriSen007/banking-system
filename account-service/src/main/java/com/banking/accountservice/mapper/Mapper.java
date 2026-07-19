package com.banking.accountservice.mapper;

import com.banking.accountservice.dto.request.AccountRequest;
import com.banking.accountservice.dto.request.UpdateAccountRequest;
import com.banking.accountservice.dto.response.AccountResponse;
import com.banking.accountservice.entity.Account;
import com.banking.accountservice.entity.AccountStatus;
import com.banking.accountservice.entity.AccountType;

import java.math.BigDecimal;

public class Mapper {
    public static AccountResponse toAccountResponse(Account accountDetails) {
        return new AccountResponse(
                accountDetails.getId(),
                accountDetails.getAccountHolderName(),
                accountDetails.getAccountNumber(),
                accountDetails.getEmail(),
                accountDetails.getPhoneNumber(),
                accountDetails.getAccountType(),
                accountDetails.getAccountStaus(),
                accountDetails.getBalance(),
                accountDetails.getDailyTransactionLimit(),
                accountDetails.getCreatedAt()
        );
    }

    public static Account toAccount(AccountRequest accountRequest, Long accountNumber) {
        return Account.builder()
                .accountHolderName(accountRequest.getAccountHolderName())
                .accountNumber(accountNumber)
                .email(accountRequest.getEmail())
                .phoneNumber(accountRequest.getPhoneNumber())
                .accountType(accountRequest.getAccountType() != null ? accountRequest.getAccountType() : AccountType.SAVINGS)
                .accountStaus(AccountStatus.ACTIVE)
                .balance(accountRequest.getInitialDeposit() != null ? accountRequest.getInitialDeposit() : BigDecimal.ZERO)
                .dailyTransactionLimit(BigDecimal.valueOf(100000))
                .build();
    }

    public static Account updateAccount(Account account, UpdateAccountRequest request) {
        if (request.getAccountHolderName() != null) {
            account.setAccountHolderName(request.getAccountHolderName().trim());
        }
        if (request.getEmail() != null) {
            account.setEmail(request.getEmail().trim());
        }
        if (request.getPhoneNumber() != null) {
            account.setPhoneNumber(request.getPhoneNumber().trim());
        }
        if (request.getAccountStatus() != null) {
            account.setAccountStaus(request.getAccountStatus());
        }

        return account;
    }
}
