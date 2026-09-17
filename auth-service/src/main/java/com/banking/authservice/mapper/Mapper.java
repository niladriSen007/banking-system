package com.banking.authservice.mapper;

import com.banking.authservice.dto.request.SignupRequest;
import com.banking.authservice.dto.response.UserResponse;
import com.banking.authservice.entity.UserEntity;


public class Mapper {

	public static UserEntity toUserEntity(SignupRequest request) {
		return UserEntity.builder()
				.firstName(request.getFirstName())
				.lastName(request.getLastName())
				.email(request.getEmail())
				.password(request.getPassword())
				.phoneNumber(request.getPhoneNumber())
				.build();
	}

	public static UserEntity toUserEntityFromUserResponse(UserResponse response) {
		return UserEntity.builder()
				.id(response.getId())
				.firstName(response.getFirstName())
				.lastName(response.getLastName())
				.email(response.getEmail())
				.phoneNumber(response.getPhoneNumber())
				.profileImage(response.getProfileImage())
				.role(response.getRole())
				.permissions(response.getUserPermissions())
				.status(response.getStatus())
				.createdAt(response.getCreatedAt())
				.lastLoggedInTime(response.getLastLoggedInTime())
				.build();
	}

	public static UserResponse toUserResponse(UserEntity entity) {
		return UserResponse.builder()
				.id(entity.getId())
				.firstName(entity.getFirstName())
				.lastName(entity.getLastName())
				.email(entity.getEmail())
				.phoneNumber(entity.getPhoneNumber())
				.profileImage(entity.getProfileImage())
				.role(entity.getRole())
				.userPermissions(entity.getPermissions())
				.status(entity.getStatus())
				.createdAt(entity.getCreatedAt())
				.lastLoggedInTime(entity.getLastLoggedInTime())
				.build();
	}
}
