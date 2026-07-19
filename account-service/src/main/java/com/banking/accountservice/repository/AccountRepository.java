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

    Optional<Account> findByAccountNumber(Long accountNumber);

//    void deleteByAccountNumber(Long accountNumber);

    @Transactional
    @Modifying
    @Query("""  
                UPDATE Account a SET a.accountStaus = "CLOSED"
            """)
    int markAccountAsClosed(Long accountNumber);


    @Transactional
    @Modifying
    @Query("""  
                UPDATE Account a SET a.accountStaus = "BLOCKED"
            """)
    int markAccountAsBlocked(Long accountNumber);
}
