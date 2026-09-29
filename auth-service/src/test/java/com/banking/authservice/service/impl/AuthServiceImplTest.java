package com.banking.authservice.service.impl;

import com.banking.authservice.entity.UserEntity;
import com.banking.authservice.repository.AuthRepository;
import com.banking.authservice.security.AppUserDetailsService;
import com.banking.authservice.security.JWTService;
import com.banking.authservice.security.UserInfoService;
import com.banking.authservice.service.ISessionService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class AuthServiceImplTest {

	@AfterEach
	void clearSecurityContext() {
		SecurityContextHolder.clearContext();
	}

	@Test
	void refreshTokenBuildsAuthenticationFromTheRefreshTokenUser() {
		AuthRepository authRepository = mock(AuthRepository.class);
		PasswordEncoder passwordEncoder = mock(PasswordEncoder.class);
		JWTService jwtService = mock(JWTService.class);
		AppUserDetailsService appUserDetailsService = mock(AppUserDetailsService.class);
		AuthenticationManager authenticationManager = mock(AuthenticationManager.class);
		ISessionService sessionService = mock(ISessionService.class);
		AuthServiceImpl authService = new AuthServiceImpl(
				authRepository,
				passwordEncoder,
				jwtService,
				appUserDetailsService,
				authenticationManager,
				sessionService);

		UserEntity user = UserEntity.builder()
				.id(123L)
				.email("user@example.com")
				.password("password")
				.build();
		UserInfoService userDetails = new UserInfoService(
				user,
				Set.of(new SimpleGrantedAuthority("ROLE_CUSTOMER")));
		HttpServletRequest request = mock(HttpServletRequest.class);
		when(request.getCookies()).thenReturn(new Cookie[]{new Cookie("refreshToken", "refresh-token")});
		when(sessionService.validateSession("refresh-token")).thenReturn(true);
		when(jwtService.getUserIdFromToken("refresh-token")).thenReturn("123");
		when(authRepository.findById("123")).thenReturn(Optional.of(user));
		when(jwtService.validateToken("refresh-token")).thenReturn(true);
		when(appUserDetailsService.loadUserByUsername(user.getEmail())).thenReturn(userDetails);
		when(jwtService.generateAccessToken(any(), eq(123L))).thenReturn("new-access-token");

		var response = authService.refreshToken(request, mock(HttpServletResponse.class));

		assertEquals("new-access-token", response.getAccessToken());
		verify(jwtService).generateAccessToken(any(UsernamePasswordAuthenticationToken.class), eq(123L));
	}
}
