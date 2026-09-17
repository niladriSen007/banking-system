package com.banking.authservice.service.impl;

import com.banking.authservice.dto.request.UpdateRequest;
import com.banking.authservice.dto.response.UserResponse;
import com.banking.authservice.entity.UserEntity;
import com.banking.authservice.mapper.Mapper;
import com.banking.authservice.repository.AuthRepository;
import com.banking.authservice.service.IUserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements IUserService {

	private final AuthRepository authRepository;

	@Override
	public UserResponse getUserByEmail(String email) {
		return authRepository.findByEmail(email)
				.map(Mapper::toUserResponse)
				.orElseThrow(() -> new RuntimeException("User not found with email: " + email));
	}

	@Override
	public UserResponse getUserById(Long id) {
		return authRepository.findById(id)
				.map(Mapper::toUserResponse)
				.orElseThrow(() -> new RuntimeException("User not found with id: " + id));
	}

	@Override
	public List<UserResponse> getAllUsers() {
		return authRepository.findAll().stream()
				.map(Mapper::toUserResponse)
				.toList();
	}

	@Override
	public UserResponse updateUser(String email, UpdateRequest updateRequest) {
		UserResponse userResponse = getUserByEmail(email);
		UserEntity userEntity = Mapper.toUserEntityFromUserResponse(userResponse);

		if (updateRequest.firstName() != null) {
			userEntity.setFirstName(updateRequest.firstName());
		}
		if (updateRequest.lastName() != null) {
			userEntity.setLastName(updateRequest.lastName());
		}
		if (updateRequest.phoneNumber() != null) {
			userEntity.setPhoneNumber(updateRequest.phoneNumber());
		}
		if (updateRequest.profileImage() != null) {
			userEntity.setProfileImage(updateRequest.profileImage());
		}

		UserEntity updatedUser = authRepository.save(userEntity);

		return Mapper.toUserResponse(updatedUser);
	}

//	@Override
//	public UserResponse suspendUser(Long userId) {
//		return authRepository.findById(userId).map(user -> {
//			user.setStatus(UserStatus.SUSPENDED);
//			user.setSuspendedAt(LocalDateTime.now());
//			authRepository.save(user);
//			return Mapper.toUserResponse(user);
//		}).orElseThrow(() -> new RuntimeException("User not found with id: " + userId));
//	}
//
//	@Override
//	public UserResponse activateUser(Long userId) {
//		return authRepository.findById(userId).map(user -> {
//			user.setStatus(UserStatus.ACTIVE);
//			user.setSuspendedAt(null);
//			authRepository.save(user);
//			return Mapper.toUserResponse(user);
//		}).orElseThrow(() -> new RuntimeException("User not found with id: " + userId));
//	}
//
//	@Override
//	public UserResponse deleteUser(Long userId) {
//		return authRepository.findById(userId).map(user -> {
//			user.setStatus(UserStatus.DELETED);
//			user.setDeletedAt(LocalDateTime.now());
//			authRepository.save(user);
//			return Mapper.toUserResponse(user);
//		}).orElseThrow(() -> new RuntimeException("User not found with id: " + userId));
//	}

}
