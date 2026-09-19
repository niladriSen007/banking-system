package com.banking.accountservice.repository;

import com.banking.accountservice.entity.Account;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Repository
public interface AccountRepository extends JpaRepository<Account,Long> {
    boolean existsByEmail(@NotBlank(message = "Account holder email must not be blank") @Email(message = "Email must be valid") String email);

    @Query(value =
            "SELECT nextval('account_number_seq')",
            nativeQuery = true)
    Long getNextAccountNumber();

    Optional<Account> findByAccountNumber(String accountNumber);

//    void deleteByAccountNumber(String accountNumber);

    @Transactional
    @Modifying
    @Query("""  
                UPDATE Account a SET a.accountStatus = "CLOSED"
            """)
    int markAccountAsClosed(String accountNumber);


    @Transactional
    @Modifying
    @Query("""  
                UPDATE Account a SET a.accountStatus = "BLOCKED"
            """)
    int markAccountAsBlocked(String accountNumber);
}
