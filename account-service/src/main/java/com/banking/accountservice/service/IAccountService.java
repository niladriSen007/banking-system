package com.banking.accountservice.service;

import com.banking.accountservice.dto.request.AccountRequest;
import com.banking.accountservice.dto.request.UpdateAccountRequest;
import com.banking.accountservice.dto.response.AccountResponse;
import jakarta.validation.Valid;

import java.math.BigDecimal;

public interface IAccountService {
    AccountResponse createAccount(@Valid AccountRequest accountRequest);

    AccountResponse getAccountDetails(@Valid Long accountNumber);

    AccountResponse updateAccountDetails(Long accountNumber, @Valid UpdateAccountRequest updateAccountRequest);

    Void deleteAccount(Long accountNumber);

    BigDecimal getAccountBalance(Long accountNumber);

    Boolean blockAccount(Long accountNumber);

    BigDecimal deductBalance(Long accountNumber, BigDecimal amount);

    BigDecimal creditBalnce(Long accountNumber, BigDecimal amount);
}
