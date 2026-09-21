package com.banking.authservice.security;

import com.banking.authservice.entity.UserEntity;
import com.banking.authservice.repository.AuthRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Slf4j
public class AppUserDetailsService implements UserDetailsService {

	private final AuthRepository authRepository;

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

		log.info("======================================");
		log.info("LOGIN EMAIL RECEIVED: {}", username);

		//Find the user in the database
		Optional<UserEntity> user = authRepository.findByEmail(username);

		log.info("USER FOUND: {}", user.isPresent());

		//If user does not exists then throw UsernameNotFound Exception
		if (user.isEmpty()) {
			log.info("No user found with username: {}", username);
			throw new UsernameNotFoundException("User with - " + username + " does not exist");
		}
		log.info("User with - {} found from database", username);

		// Add authorities
		Set<GrantedAuthority> grantedAuthority = new HashSet<>(Set.of(new SimpleGrantedAuthority(user.get().getRole().name())));
		user.get().getPermissions().forEach(permission -> grantedAuthority.add(new SimpleGrantedAuthority(permission.name())));
//        Collection<GrantedAuthority> grantedAuthorities = grantedAuthority;

		return new UserInfoService(
				user.get(),
				grantedAuthority
		);
	}


	public UserInfoService loadUserByUserId(String id) {
		//Find the user in the database
		Optional<UserEntity> user = authRepository.findById(Long.valueOf(id));

		//If user does not exists then throw UsernameNotFound Exception
		if (user.isEmpty()) {
			log.info("No user found with id: " + id);
			throw new UsernameNotFoundException("User with - " + id + " does not exist");
		}
		log.info("User with - " + id + " found from database");

		// Add authorities
		Set<GrantedAuthority> grantedAuthorities = new HashSet<>(Set.of(new SimpleGrantedAuthority(user.get().getRole().name())));
		user.get().getPermissions().forEach(permission -> grantedAuthorities.add(new SimpleGrantedAuthority(permission.name())));

		return new UserInfoService(
				user.get(),
				grantedAuthorities
		);
	}
}
