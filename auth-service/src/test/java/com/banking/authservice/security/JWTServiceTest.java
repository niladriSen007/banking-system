package com.banking.authservice.security;

import com.banking.authservice.config.JwtProperties;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.ResourceLoader;

import java.nio.charset.StandardCharsets;
import java.security.KeyPair;
import java.security.KeyPairGenerator;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class JWTServiceTest {

	@Test
	void generatedRefreshTokenContainsConfiguredAudience() throws Exception {
		KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
		keyPairGenerator.initialize(2048);
		KeyPair keyPair = keyPairGenerator.generateKeyPair();

		JwtProperties properties = new JwtProperties();
		properties.setIssuer("banking-auth-service");
		properties.setAudience("banking-api");
		properties.setClockSkewSeconds(30);
		properties.setPublicKeyLocation("classpath:public-key.pem");

		ResourceLoader resourceLoader = mock(ResourceLoader.class);
		String publicKeyPem = """
				-----BEGIN PUBLIC KEY-----
				%s
				-----END PUBLIC KEY-----
				""".formatted(java.util.Base64.getMimeEncoder(64, new byte[]{'\n'})
				.encodeToString(keyPair.getPublic().getEncoded()));
		when(resourceLoader.getResource(properties.getPublicKeyLocation()))
				.thenReturn(new ByteArrayResource(publicKeyPem.getBytes(StandardCharsets.UTF_8)));

		JWTService jwtService = new JWTService(properties, keyPair.getPrivate(), resourceLoader);
		String refreshToken = jwtService.generateRefreshToken(null, 123L);

		assertEquals("123", jwtService.getUserIdFromToken(refreshToken));
	}
}
