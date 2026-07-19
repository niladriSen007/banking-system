package com.banking.accountservice.dto.request;

import com.banking.accountservice.entity.AccountType;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AccountRequest {


    @NotBlank(message = "Account Holder name must not be blank")
    private String accountHolderName;

    @NotBlank(message = "Account holder email must not be blank")
    @Email(message = "Email must be valid")
    private String email;

    @NotBlank(message = "Account holder phone number must not be blank")
    private String phoneNumber;

    private AccountType accountType;

    @Positive(message = "Balance should be greater than 0")
    private BigDecimal initialDeposit;

}
