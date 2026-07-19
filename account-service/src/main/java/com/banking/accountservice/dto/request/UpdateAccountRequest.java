package com.banking.accountservice.dto.request;

import com.banking.accountservice.entity.AccountStatus;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UpdateAccountRequest {
    private String accountHolderName;
    private String email;
    private String phoneNumber;
    private AccountStatus accountStatus;
}
