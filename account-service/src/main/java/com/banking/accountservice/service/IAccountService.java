package com.banking.accountservice.service;

import com.banking.accountservice.dto.request.AccountRequest;
import com.banking.accountservice.dto.request.UpdateAccountRequest;
import com.banking.accountservice.dto.response.AccountResponse;
import jakarta.validation.Valid;

import java.math.BigDecimal;

public interface IAccountService {
    AccountResponse createAccount(@Valid AccountRequest accountRequest);

    AccountResponse getAccountDetails(@Valid String accountNumber);

    AccountResponse updateAccountDetails(String accountNumber, @Valid UpdateAccountRequest updateAccountRequest);

    Void deleteAccount(String accountNumber);

    BigDecimal getAccountBalance(String accountNumber);

    Boolean blockAccount(String accountNumber);

    BigDecimal deductBalance(String accountNumber, BigDecimal amount);

    BigDecimal creditBalnce(String accountNumber, BigDecimal amount);
}
