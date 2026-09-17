package com.banking.authservice.service.impl;

import com.banking.authservice.entity.Session;
import com.banking.authservice.repository.SessionRepository;
import com.banking.authservice.service.ISessionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.web.authentication.session.SessionAuthenticationException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class SessionServiceImpl implements ISessionService {

	private final SessionRepository sessionRepository;
	private final int MAX_SESSIONS_PER_USER = 2;

	@Override
	public void generateNewSession(String email, String refreshToken) {
		List<Session> sessionsByUser = sessionRepository.findByUserEmail(email);
		if (sessionsByUser != null && sessionsByUser.size() == MAX_SESSIONS_PER_USER) {
			sessionsByUser.sort(Comparator.comparing(Session::getLastUsedAt));
			Session oldestSession = sessionsByUser.get(0);
			sessionRepository.delete(oldestSession);
		}
		Session build = Session.builder().userEmail(email).refreshToken(refreshToken).lastUsedAt(LocalDateTime.now()).build();
		sessionRepository.save(build);
	}

	@Override
	public boolean validateSession(String refreshToken) {
		Session session = sessionRepository.findByRefreshToken(refreshToken).orElseThrow(
				() -> new SessionAuthenticationException("Invalid session")
		);
		session.setLastUsedAt(LocalDateTime.now());
		sessionRepository.save(session);
		return session != null;
	}
}