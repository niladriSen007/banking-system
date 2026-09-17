package com.banking.apigateway.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@ConfigurationProperties(prefix = "jwt")
public class JwtProperties {

	private String issuer;

	private String audience;

	private String publicKeyLocation;

	private long clockSkewSeconds = 30;

	private List<String> publicPaths = new ArrayList<>();
}
