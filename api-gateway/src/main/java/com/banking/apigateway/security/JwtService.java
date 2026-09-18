package com.banking.apigateway.security;

import com.banking.apigateway.config.JwtProperties;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.spec.X509EncodedKeySpec;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class JwtService {

	private final JwtProperties jwtProperties;

	private final PublicKey publicKey;

	public JwtService(JwtProperties jwtProperties, ResourceLoader resourceLoader) {
		this.jwtProperties = jwtProperties;
		this.publicKey = loadPublicKey(
				resourceLoader,
				jwtProperties.getPublicKeyLocation()
		);
	}


	public JwtValidationResult validateToken(String token) {

		try {
			Jws<Claims> claimsJws = Jwts.parser()
					.verifyWith(publicKey)
					.requireIssuer(jwtProperties.getIssuer())
					.requireAudience(jwtProperties.getAudience())
					.clockSkewSeconds(jwtProperties.getClockSkewSeconds())
					.build()
					.parseSignedClaims(token);
//					.getPayload();

			System.out.println(claimsJws);

			Claims claims = claimsJws.getPayload();

			String userId = claims.getSubject(); // or claims.get("userId", String.class);
			System.out.println(userId);
			String username =
					claims.get("email", String.class);
			String role =
					claims.get("authorities", String.class);
			Set<String> authorities =
					extractAuthorities(claims);

			if (userId == null || userId.isBlank()) {
				return JwtValidationResult.invalid();
			}

			return JwtValidationResult.valid(
					userId,
					username,
//					role,
					authorities
			);

		} catch (JwtException |
		         IllegalArgumentException exception) {
			System.out.println("JWT validation failed");
			System.out.println("Exception: " + exception.getClass().getName());
			System.out.println("Message: " + exception.getMessage());
			exception.printStackTrace();
			return JwtValidationResult.invalid();
		}
	}

	private Set<String> extractAuthorities(Claims claims) {

		Object authoritiesObject =
				claims.get("authorities");

		if (authoritiesObject instanceof List<?> list) {
			Set<String> authorities = new HashSet<>();
			for (Object value : list) {
				if (value instanceof String authority) {
					authorities.add(authority);
				}
			}
			return Collections.unmodifiableSet(authorities);
		}
		return Set.of();
	}


	/**
	 * Loads the public key from the specified location.
	 *
	 * @param resourceLoader
	 * @param location
	 * @return
	 */
	private PublicKey loadPublicKey(
			ResourceLoader resourceLoader,
			String location) {

		try {

			/**
			 * Load the public key from the specified location
			 */
			Resource resource =
					resourceLoader.getResource(location);

			/**
			 * Read the public key from the resource and convert it to a PublicKey object
			 */
			try (InputStream inputStream =
					     resource.getInputStream()) {

				// Read the public key from the input stream and convert it to a PublicKey object
				String key =
						new String(
								inputStream.readAllBytes(),
								StandardCharsets.UTF_8
						);

				// Remove the "BEGIN PUBLIC KEY" and "END PUBLIC KEY" lines, as well as any whitespace characters, from the key string
				String publicKeyPEM =
						key
								.replace(
										"-----BEGIN PUBLIC KEY-----",
										""
								)
								.replace(
										"-----END PUBLIC KEY-----",
										""
								)
								.replaceAll(
										"\\s+",
										""
								);

				// Decode the Base64-encoded public key and create a PublicKey object from it
				byte[] decoded =
						Decoders.BASE64.decode(publicKeyPEM);

				// Create a PublicKey object from the decoded key bytes
				X509EncodedKeySpec keySpec =
						new X509EncodedKeySpec(decoded);

				// Create a KeyFactory for the RSA algorithm
				KeyFactory keyFactory =
						KeyFactory.getInstance("RSA");

				// Generate a PublicKey object from the key specification
				return keyFactory.generatePublic(keySpec);
			}

		} catch (Exception exception) {

			throw new IllegalStateException(
					"Unable to load JWT public key",
					exception
			);
		}
	}
}
