package com.banking.authservice.service;

import com.banking.authservice.dto.request.LoginRequest;
import com.banking.authservice.dto.request.SignupRequest;
import com.banking.authservice.dto.response.LoginResponse;
import com.banking.authservice.dto.response.RefreshResponse;
import com.banking.authservice.dto.response.SignupResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public interface IAuthService {
	SignupResponse signup(SignupRequest signupRequest);

	LoginResponse login(LoginRequest loginRequest);

	RefreshResponse refreshToken(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse);
}
