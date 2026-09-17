package com.banking.apigateway.filter;

import com.banking.apigateway.config.JwtProperties;
import com.banking.apigateway.security.JwtService;
import com.banking.apigateway.security.JwtValidationResult;
import lombok.RequiredArgsConstructor;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.List;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationGlobalFilter implements GlobalFilter, Ordered {


	private final JwtService jwtService;
	private final JwtProperties jwtProperties;
	private final AntPathMatcher pathMatcher;

	@Override
	public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {

		String path = exchange.getRequest().getPath().value();

		/*
		 * 1. Public endpoint?
		 */
		if (isPublicPath(path)) {
			return chain.filter(exchange);
		}


		/*
		 * 2. Extract Authorization header.
		 */
		String authorization =
				exchange.getRequest()
						.getHeaders()
						.getFirst(
								HttpHeaders.AUTHORIZATION
						);

		/*
		 * 3. Missing JWT.
		 */
		if (authorization == null ||
				!authorization.startsWith("Bearer ")) {

			return unauthorized(exchange);
		}

		/*
		 * 4. Extract token.
		 */
		String token =
				authorization.substring(7).trim();

		if (token.isBlank()) {
			return unauthorized(exchange);
		}

		/*
		 * 5. Validate JWT.
		 */
		JwtValidationResult result =
				jwtService.validateToken(token);

		/*
		 * 6. Invalid JWT.
		 */
		if (!result.valid()) {
			return unauthorized(exchange);
		}


		/*
		 * 7. Remove any client-supplied identity headers.
		 *
		 * We NEVER trust:
		 *
		 * X-User-Id
		 * X-Username
		 * X-Role
		 *
		 * from the client.
		 */
		ServerHttpRequest request =
				exchange.getRequest()
						.mutate()
						.headers(headers -> {

							headers.remove("X-User-Id");
							headers.remove("X-Username");
							headers.remove("X-Role");
							headers.remove("X-Authorities");

							/*
							 * Add trusted identity information
							 * obtained from the validated JWT.
							 */
							headers.add(
									"X-User-Id",
									result.userId()
							);

							if (result.username() != null) {

								headers.add(
										"X-Username",
										result.username()
								);
							}

							if (result.role() != null) {

								headers.add(
										"X-Role",
										result.role()
								);
							}

							if (!result.authorities().isEmpty()) {
								headers.add(
										"X-Authorities",
										String.join(
												",",
												result.authorities()
										)
								);
							}
						})
						.build();

		/*
		 * 8. Continue to downstream service.
		 */
		return chain.filter(
				exchange.mutate()
						.request(request)
						.build()
		);
	}


	private boolean isPublicPath(String path) {

		List<String> publicPaths =
				jwtProperties.getPublicPaths();

		return publicPaths.stream()
				.anyMatch(pattern ->
						pathMatcher.match(pattern, path)
				);
	}


	private Mono<Void> unauthorized(
			ServerWebExchange exchange) {

		var response =
				exchange.getResponse();

		response.setStatusCode(
				HttpStatus.UNAUTHORIZED
		);

		response.getHeaders()
				.setContentType(
						MediaType.APPLICATION_JSON
				);

		String body = """
				{
				    "status": 401,
				    "error": "Unauthorized",
				    "message": "Invalid or missing JWT"
				}
				""";

		var buffer =
				response.bufferFactory()
						.wrap(body.getBytes());

		return response.writeWith(
				Mono.just(buffer)
		);
	}

	@Override
	public int getOrder() {
		return -100;
	}
}
