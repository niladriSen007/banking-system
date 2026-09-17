package com.banking.authservice.security;


import com.banking.authservice.entity.UserEntity;
import org.jspecify.annotations.Nullable;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

public class UserInfoService implements UserDetails {

	private final String email;
	private final String password;
	private final Collection<? extends GrantedAuthority> authorities;

	public UserInfoService(UserEntity userDetails, Collection<? extends GrantedAuthority> authorities) {
		this.email = userDetails.getEmail();
		this.password = userDetails.getPassword();
		this.authorities = authorities;
	}

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		return authorities;
	}

	@Override
	public @Nullable String getPassword() {
		return password;
	}

	@Override
	public String getUsername() {
		return email;
	}
}
