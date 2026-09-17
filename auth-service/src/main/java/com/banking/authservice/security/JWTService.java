package com.banking.authservice.security;

import com.banking.authservice.config.JwtProperties;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.X509EncodedKeySpec;
import java.time.Instant;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Slf4j
public class JWTService {

	private final JwtProperties jwtProperties;
	private final PrivateKey privateKey;
	private final PublicKey publicKey;

	public JWTService(JwtProperties jwtProperties,PrivateKey privateKey, ResourceLoader resourceLoader) {
		this.jwtProperties = jwtProperties;
		this.privateKey = privateKey;
		this.publicKey = loadPublicKey(
				resourceLoader,
				jwtProperties.getPublicKeyLocation()
		);
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

//	private final SecretKey secretKey = Keys.hmacShaKeyFor(
//			JWTConstants.SECRET_KEY.getBytes(StandardCharsets.UTF_8)
//	);

	Instant now = Instant.now();

	Instant expiration =
			now.plusSeconds(
					jwtProperties
							.getAccessTokenExpirationSeconds()
			);

	public String generateAccessToken(Authentication authentication, Long userId) {
		return generateToken(authentication, userId);
	}

	public String generateRefreshToken(Authentication authentication, Long userId) {
		return Jwts.builder().
				subject(userId.toString())
				.issuer(jwtProperties.getIssuer())
				.issuedAt(new Date())
				.expiration(
						new Date(System.currentTimeMillis() + 1000L * 60 * 60 * 24 * 30 * 6) // 6 months
				)
				.signWith(privateKey, Jwts.SIG.RS256)
				.compact();
	}

	private String generateToken(Authentication authentication, Long userId) {
		Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
		String roles = populateAuthorities(authorities);

		return Jwts.builder()
				.subject(userId.toString())
				.issuer(jwtProperties.getIssuer())
				.audience()
				.add(jwtProperties.getAudience())
				.and()
				.claim("email", authentication.getName())
				.claim("authorities", roles)
				.claim("userId", userId)
				.issuedAt(new Date())
				.expiration(Date.from(expiration)) // 15 minute
				.signWith(privateKey, Jwts.SIG.RS256)
				.compact();
	}

	private String populateAuthorities(Collection<? extends GrantedAuthority> authorities) {

		Set<String> authoritiesSet = new HashSet<>();
		for (GrantedAuthority grantedAuthority : authorities) {
			authoritiesSet.add(grantedAuthority.getAuthority());
		}

		return String.join(",", authoritiesSet);
	}

	public String getUserIdFromToken(String token) {

		Claims claims = Jwts.parser()
				.verifyWith(publicKey)
				.requireIssuer(jwtProperties.getIssuer())
				.requireAudience(jwtProperties.getAudience())
				.clockSkewSeconds(jwtProperties.getClockSkewSeconds())
				.build()
				.parseSignedClaims(token)
				.getPayload();

		return claims.getSubject();
	}

	public boolean validateToken(String token) {
		String userId = getUserIdFromToken(token);
		try {
			Jwts.parser()
					.verifyWith(publicKey)
					.build()
					.parseSignedClaims(token);
			return true;
		} catch (Exception e) {
			return false;
		}
	}

}
