package com.banking.authservice.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.Base64;

@Configuration
public class RsaKeyLoader {

	@Bean
	public PrivateKey privateKey(
			JwtProperties jwtProperties,
			ResourceLoader resourceLoader) {

		try {

			Resource resource =
					resourceLoader.getResource(
							jwtProperties.getPrivateKeyLocation()
					);

			try (InputStream inputStream =
					     resource.getInputStream()) {
				String key =
						new String(
								inputStream.readAllBytes(),
								StandardCharsets.UTF_8
						);

				String privateKeyPEM =
						key
								.replace(
										"-----BEGIN PRIVATE KEY-----",
										""
								)
								.replace(
										"-----END PRIVATE KEY-----",
										""
								)
								.replaceAll(
										"\\s+",
										""
								);

				byte[] decoded =
						Base64.getDecoder()
								.decode(privateKeyPEM);

				PKCS8EncodedKeySpec keySpec =
						new PKCS8EncodedKeySpec(decoded);

				KeyFactory keyFactory =
						KeyFactory.getInstance("RSA");

				return keyFactory.generatePrivate(
						keySpec
				);
			}

		} catch (Exception exception) {

			throw new IllegalStateException(
					"Unable to load JWT private key",
					exception
			);
		}
	}
}
