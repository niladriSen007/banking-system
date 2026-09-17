package com.banking.authservice.service;

import com.banking.authservice.dto.request.UpdateRequest;
import com.banking.authservice.dto.response.UserResponse;

import java.util.List;



public interface IUserService {
	UserResponse getUserByEmail(String email);

	UserResponse getUserById(Long id);

	List<UserResponse> getAllUsers();

	UserResponse updateUser(String email, UpdateRequest updateRequest);

//	// Admin methods
//	UserResponse suspendUser(Long userId);
//
//	UserResponse activateUser(Long userId);
//
//	UserResponse deleteUser(Long userId);
}
