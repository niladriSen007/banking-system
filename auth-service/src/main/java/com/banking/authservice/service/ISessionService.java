package com.banking.authservice.service;

public interface ISessionService {
	public void generateNewSession(String email, String refreshToken);

	public boolean validateSession(String refreshToken);
}