package com.banking.authservice.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SignupResponse{
	String title;
	String message;
	UserResponse userResponse;
}

