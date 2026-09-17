package com.banking.authservice.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@ConfigurationProperties(prefix = "jwt")
public class JwtProperties {

	private String issuer;

	private String audience;

	private long clockSkewSeconds;

	private String privateKeyLocation;

	private String publicKeyLocation;

	private long accessTokenExpirationSeconds = 900;
}