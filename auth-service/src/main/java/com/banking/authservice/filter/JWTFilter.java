package com.banking.authservice.filter;

import com.banking.authservice.security.AppUserDetailsService;
import com.banking.authservice.security.JWTService;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.servlet.HandlerExceptionResolver;

import java.io.IOException;
import java.util.Set;

@Component
@RequiredArgsConstructor
@Slf4j
public class JWTFilter extends OncePerRequestFilter {

	private static final Set<String> WHITELIST = Set.of(
			"/api/v1/auth/login", "/api/v1/auth/signup" , "/api/v1/auth/refresh-token"
	);
	private final AppUserDetailsService appUserDetailsService;
	private final JWTService jwtService;
	@Autowired
	@Qualifier("handlerExceptionResolver")
	private HandlerExceptionResolver handlerExceptionResolver;

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
		try {
			String path = request.getServletPath();

			if (WHITELIST.contains(path)) {
				log.info("Skip the filter");
				filterChain.doFilter(request, response);
				return;
			}

			String authHeader = request.getHeader("Authorization");
			String token = "";
			String userId = null;

			if (authHeader == null || !authHeader.startsWith("Bearer ")) {
				filterChain.doFilter(request, response);
				return;
			} else {
				token = authHeader.substring(7);
				userId = jwtService.getUserIdFromToken(token);
			}

			if (token == null) {
				Cookie[] cookies = request.getCookies();
				if (cookies != null) {
					for (Cookie cookie : cookies) {
						if ("jwtToken".equals(cookie.getName())) {
							token = cookie.getValue();
							break;
						}
					}
				}
			}

			if (userId != null && SecurityContextHolder.getContext().getAuthentication() == null) {
				UserDetails userDetails = appUserDetailsService.loadUserByUserId(userId);
				if (jwtService.validateToken(token)) {
					UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
							userDetails,
							null,
							userDetails.getAuthorities());

					// this will have all the details of the user like - IP address, session id, etc.
					authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
					log.info("User " + userId + " authenticated, setting security context");
					log.info("Authtoken: " + authToken.getName());
					SecurityContextHolder.getContext().setAuthentication(authToken);
				}
			}

			filterChain.doFilter(request, response);

		} catch (Exception ex) {
			handlerExceptionResolver.resolveException(request, response, null, ex);
		}
	}
}
