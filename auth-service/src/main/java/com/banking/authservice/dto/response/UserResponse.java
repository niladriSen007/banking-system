package com.banking.authservice.dto.response;

import com.banking.authservice.entity.UserPermission;
import com.banking.authservice.entity.UserRole;
import com.banking.authservice.entity.UserStatus;

import java.time.LocalDateTime;
import java.util.Set;

//public record UserResponse(
//		Long id,
//		String email,
//		String firstName,
//		String lastName,
//		String phoneNumber,
//		String profileImage,
//		UserRole role,
//		Set<UserPermission> userPermissions,
//		UserStatus status,
//		LocalDateTime createdAt,
//		LocalDateTime lastLoggedInTime
//) {
//
//}


import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.Set;

@Data
@Builder
public class UserResponse {
	private Long id;
	private String email;
	private String firstName;
	private String lastName;
	private String phoneNumber;
	private String profileImage;
	private UserRole role;
	private Set<UserPermission> userPermissions;
	private UserStatus status;
	private LocalDateTime createdAt;
	private LocalDateTime lastLoggedInTime;

}