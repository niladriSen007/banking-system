package com.banking.authservice.controller;


import com.banking.authservice.dto.request.LoginRequest;
import com.banking.authservice.dto.request.SignupRequest;
import com.banking.authservice.dto.response.ApiResponse;
import com.banking.authservice.dto.response.LoginResponse;
import com.banking.authservice.dto.response.RefreshResponse;
import com.banking.authservice.dto.response.SignupResponse;
import com.banking.authservice.service.IAuthService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Duration;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/auth")
@Slf4j
public class AuthController {

	private final IAuthService authService;

	@PostMapping("/signup")
	public ResponseEntity<ApiResponse<SignupResponse>> signup(@RequestBody @Valid SignupRequest signupRequest) {
		log.info("Signup request received in controller - AuthController");
		return ResponseEntity.ok(ApiResponse.success(authService.signup(signupRequest), 201));
	}

	@PostMapping("/login")
	public ResponseEntity<ApiResponse<LoginResponse>> login(@RequestBody @Valid LoginRequest loginRequest) {
		log.info("Login request received in controller - AuthController");
		LoginResponse loginResponse = authService.login(loginRequest);

		// Storing the refresh token into the cookie for the next 180 days
		ResponseCookie refreshCookie = ResponseCookie.from("refreshToken", loginResponse.getRefreshToken())
				.httpOnly(true)
				.path("/")
				.maxAge(Duration.ofDays(180)) // 180 days
				.sameSite("Strict")
				.build();

		// Storing the access token into the cookie for the next 15 minutes
		ResponseCookie accessCookie = ResponseCookie.from("accessToken", loginResponse.getAccessToken())
				.httpOnly(true)
				.path("/")
				.maxAge(Duration.ofMinutes(15))
				.sameSite("Strict")
				.build();

		return ResponseEntity.ok()
				.header(HttpHeaders.SET_COOKIE, refreshCookie.toString())
				.header(HttpHeaders.SET_COOKIE, accessCookie.toString())
				.body(ApiResponse.success(loginResponse, 200));
	}

	@PostMapping("/refresh-token")
	public ResponseEntity<ApiResponse<RefreshResponse>> refreshToken(HttpServletRequest request, HttpServletResponse response) {
		return ResponseEntity.ok(ApiResponse.success(authService.refreshToken(request, response), 200));
	}

	@GetMapping("/hi")
	public ResponseEntity<ApiResponse<String>> hi() {
		return ResponseEntity.ok(ApiResponse.success("Hi"));
	}
}
