package com.banking.authservice.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor

public class SignupRequest {
	@NotBlank(message = "First name is mandatory")
	private String firstName;

	@NotBlank(message = "Last name is mandatory")
	private String lastName;

	@Email(message = "Email must be valid")
	@NotBlank(message = "Email is mandatory")
	private String email;

	@NotBlank(message = "Phone is mandatory")
	private String password;

	private String phoneNumber;
}
