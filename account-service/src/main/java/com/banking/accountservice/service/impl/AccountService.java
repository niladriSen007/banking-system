package com.banking.accountservice.service.impl;

import com.banking.accountservice.entity.AccountStatus;
import com.banking.accountservice.exceptions.AccountNotActiveForTransactionsException;
import com.banking.accountservice.exceptions.NotEnoughBalanceException;
import com.banking.accountservice.mapper.Mapper;
import com.banking.accountservice.dto.request.AccountRequest;
import com.banking.accountservice.dto.request.UpdateAccountRequest;
import com.banking.accountservice.dto.response.AccountResponse;
import com.banking.accountservice.entity.Account;
import com.banking.accountservice.exceptions.AccountAlreadyExistsException;
import com.banking.accountservice.exceptions.AccountDoesNotExistException;
import com.banking.accountservice.repository.AccountRepository;
import com.banking.accountservice.service.IAccountService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
@Transactional
public class AccountService implements IAccountService {

    //TODO: Implement Bloom Filter

    AccountRepository accountRepository;

    @Override
    public AccountResponse createAccount(AccountRequest accountRequest) {
        log.info("Creating account - Service");

        boolean isAccountExistsByEmail = accountRepository.existsByEmail(accountRequest.getEmail());
        if (!isAccountExistsByEmail) {
            throw new AccountAlreadyExistsException("Account with email " + accountRequest.getEmail() + " already exists");
        }

        Account accountDetails = Mapper.toAccount(accountRequest, generateAccountNumber());
        Account newAccount = accountRepository.save(accountDetails);

        return Mapper.toAccountResponse(newAccount);
    }

    private Long generateAccountNumber() {
        return accountRepository.getNextAccountNumber();
    }

    @Override
    @Transactional(readOnly = true)
    public AccountResponse getAccountDetails(Long accountNumber) {
        return accountRepository.findByAccountNumber(accountNumber).
                map(Mapper::toAccountResponse).
                orElseThrow(() -> new AccountDoesNotExistException("Account not found with given account number"));
    }

    @Override
    public AccountResponse updateAccountDetails(Long accountNumber, UpdateAccountRequest updateAccountRequest) {
        Account account = accountRepository.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new AccountDoesNotExistException("Account not found with given account number"));

        if (accountRepository.existsByEmail(updateAccountRequest.getEmail())) {
            throw new AccountAlreadyExistsException("Account with email " + updateAccountRequest.getEmail() + " already exists");
        }

        Account updatedAccountDetails = Mapper.updateAccount(account, updateAccountRequest);
        Account updatedAccount = accountRepository.save(updatedAccountDetails);
        return Mapper.toAccountResponse(updatedAccount);
    }

    @Override
    public Void deleteAccount(Long accountNumber) {
        int affectedRows = accountRepository.markAccountAsClosed(accountNumber);
        if (affectedRows == 0) {
            throw new AccountDoesNotExistException("Account not found with given account number");
        }
        log.info("Account with account number {} has been deleted", accountNumber);
        return null;
    }

    @Override
    @Transactional(readOnly = true)
    public BigDecimal getAccountBalance(Long accountNumber) {
        return accountRepository.findByAccountNumber(accountNumber)
                .map(Account::getBalance)
                .orElseThrow(() -> new AccountDoesNotExistException("Account not found with given account number"));
    }

    @Override
    public Boolean blockAccount(Long accountNumber) {
        int affectedRows = accountRepository.markAccountAsBlocked(accountNumber);
        if (affectedRows == 0) {
            throw new AccountDoesNotExistException("Account not found with given account number");
        }
        log.info("Account with account number {} has been blocked", accountNumber);
        return true;
    }

    @Override
    public BigDecimal deductBalance(Long accountNumber, BigDecimal amount) {
        Account account = accountRepository.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new AccountDoesNotExistException("Account not found with given account number"));
        if (!account.getAccountStaus().equals(AccountStatus.ACTIVE)) {
            throw new AccountNotActiveForTransactionsException("Account not active for this account");
        }
        if (account.getBalance().compareTo(amount) < 0) {
            throw new NotEnoughBalanceException("Not enough balance for this account");
        }
        account.setBalance(account.getBalance().subtract(amount));
        Account updatedBalanceDetails = accountRepository.save(account);
        return updatedBalanceDetails.getBalance();
    }

    @Override
    public BigDecimal creditBalnce(Long accountNumber, BigDecimal amount) {
        Account account = accountRepository.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new AccountDoesNotExistException("Account not found with given account number"));
        if (!account.getAccountStaus().equals(AccountStatus.ACTIVE)) {
            throw new AccountNotActiveForTransactionsException("Account not active for this account");
        }
        account.setBalance(account.getBalance().add(amount));
        Account updatedBalanceDetails = accountRepository.save(account);
        return updatedBalanceDetails.getBalance();
    }

}
