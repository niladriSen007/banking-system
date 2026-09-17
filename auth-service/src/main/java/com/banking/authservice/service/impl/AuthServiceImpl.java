package com.banking.authservice.service.impl;


import com.banking.authservice.dto.request.LoginRequest;
import com.banking.authservice.dto.request.SignupRequest;
import com.banking.authservice.dto.response.LoginResponse;
import com.banking.authservice.dto.response.RefreshResponse;
import com.banking.authservice.dto.response.SignupResponse;
import com.banking.authservice.entity.UserEntity;
import com.banking.authservice.entity.UserRole;
import com.banking.authservice.exceptions.InvalidCredentialsException;
import com.banking.authservice.exceptions.UserAlreadyExistsException;
import com.banking.authservice.mapper.Mapper;
import com.banking.authservice.repository.AuthRepository;
import com.banking.authservice.security.AppUserDetailsService;
import com.banking.authservice.security.JWTService;
import com.banking.authservice.security.UserInfoService;
import com.banking.authservice.service.IAuthService;
import com.banking.authservice.service.ISessionService;
import com.banking.authservice.utils.RolePermissionMapping;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Optional;


@Service
@RequiredArgsConstructor
@Slf4j
public class AuthServiceImpl implements IAuthService {

	private final AuthRepository authRepository;
	private final PasswordEncoder passwordEncoder;
	private final JWTService jwtService;
	private final AppUserDetailsService appUserDetailsService;
	private final AuthenticationManager authenticationManager;
	private final ISessionService sessionService;

	@Override
	public SignupResponse signup(SignupRequest signupRequest) {
		log.info("Signup request received in service - AuthServiceImpl");

		// Firstly we will have to check the user email already exists in the database
		// or not
//        Optional<UserEntity> userEntity = authRepository.findByEmail(signupRequest.getEmail());
		boolean userExists = authRepository.existsByEmail(signupRequest.getEmail());

		if (userExists) {
			throw new UserAlreadyExistsException("User already exists for the user - " + signupRequest.getEmail());
		}

		// Creating the UserEntity from the request payload
		UserEntity user = UserEntity.builder()
				.email(signupRequest.getEmail())
				.password(passwordEncoder.encode(signupRequest.getPassword()))
				.firstName(signupRequest.getFirstName())
				.lastName(signupRequest.getLastName())
				.phoneNumber(signupRequest.getPhoneNumber())
				.permissions(RolePermissionMapping.getPermissionsByRole(UserRole.CUSTOMER))
				.lastLoggedInTime(LocalDateTime.now())
				.build();

		// Saving the user into the database
		UserEntity registeredUser = authRepository.save(user);

		// Mapping as per the AuthResponse
		SignupResponse signupResponse = SignupResponse.builder()
				.title("Welcome to the Job Portal")
				.message("Registered successfully")
				.userResponse(Mapper.toUserResponse(registeredUser))
				.build();

		// Return the result
		return signupResponse;
	}

	@Override
	public LoginResponse login(LoginRequest loginRequest) {

		log.info("Login request received in service - AuthServiceImpl");

		Authentication authentication = authenticateUser(loginRequest.getEmail(), loginRequest.getPassword());

		// SecurityContextHolder.getContext().setAuthentication(authentication);

		// Optional<UserEntity> user =
		// authRepository.findByEmail(loginRequest.getEmail());
		UserInfoService principal = (UserInfoService) authentication.getPrincipal();
		Optional<UserEntity> user = authRepository.findByEmail(principal.getUsername());

		if (user.isEmpty())
			throw new BadCredentialsException("Invalid username or password");

		String accessToken = jwtService.generateAccessToken(authentication, user.get().getId());
		String refreshToken = jwtService.generateRefreshToken(authentication, user.get().getId());
		sessionService.generateNewSession(user.get().getEmail(), refreshToken);
		user.get().setLastLoggedInTime(LocalDateTime.now());
		authRepository.save(user.get());

		return LoginResponse.builder()
				.title("Welcome to the Job Portal")
				.message("Logged in successfully")
				.userResponse(Mapper.toUserResponse(user.get()))
				.accessToken(accessToken)
				.refreshToken(refreshToken)
				.build();
	}

	@Override
	public RefreshResponse refreshToken(HttpServletRequest httpServletRequest,
	                                    HttpServletResponse httpServletResponse) {
		log.info("Attempting to refresh token");
		Cookie[] cookies = httpServletRequest.getCookies();
		if (cookies == null || cookies.length == 0) {
			log.warn("No cookies present on refresh-token request");
			throw new AuthenticationServiceException("Refresh token not found");
		}
		Cookie refreshToken = Arrays.stream(cookies)
				.filter(cookie -> cookie.getName().equals("refreshToken"))
				.findFirst()
				.orElseThrow(() -> new AuthenticationServiceException("Refresh token not found"));
		log.info("Received request to refresh token");
		String refreshTokenValue = refreshToken.getValue();
		log.info("Refresh token value: {}", refreshTokenValue);

		boolean isValidSession = sessionService.validateSession(refreshTokenValue);

		if (!isValidSession)
			throw new InvalidCredentialsException("Invalid refresh token");

		String userIdFromToken = jwtService.getUserIdFromToken(refreshTokenValue);
		Optional<UserEntity> userFromToken = authRepository.findById(userIdFromToken);
		if (userFromToken.isEmpty())
			throw new AuthenticationServiceException("User not found");
		// UserDetails userDetails =
		// appUserDetailsService.loadUserByUsername(userFromToken.get().getEmail());
		if (!jwtService.validateToken(refreshTokenValue)) {
			throw new JwtException("Invalid refresh token");
		}
		String accessToken = jwtService.generateAccessToken(SecurityContextHolder.getContext().getAuthentication(),
				Long.valueOf(userIdFromToken));
		log.info("Refresh token validated and new tokens generated for user: {}", userFromToken.get().getEmail());
		return RefreshResponse.builder()
				.title("Access token generated successfully")
				.message("Token generation successful")
				.accessToken(accessToken)
				.refreshToken(refreshTokenValue)
				.build();
	}

	private Authentication authenticateUser(String email, String password){

		UserDetails userDetails = appUserDetailsService.loadUserByUsername(email);

		if (userDetails == null) {
			throw new UsernameNotFoundException("User not found with username - " + email);
		}

		if (!passwordEncoder.matches(password, userDetails.getPassword())) {
			throw new InvalidCredentialsException("Invalid password");
		}

		return authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(email, password));

	}
}
