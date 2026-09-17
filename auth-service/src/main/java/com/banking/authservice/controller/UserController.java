package com.banking.authservice.controller;

import com.banking.authservice.dto.request.UpdateRequest;
import com.banking.authservice.dto.response.ApiResponse;
import com.banking.authservice.dto.response.UserResponse;
import com.banking.authservice.service.IUserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/users")
@Slf4j
public class UserController {
	private final IUserService userService;

	@GetMapping("/profile")
	public ResponseEntity<ApiResponse<UserResponse>> getUserProfile(@RequestHeader("X-User-Email") String email) {
		return ResponseEntity.ok(ApiResponse.success(userService.getUserByEmail(email), 200));
	}

	@PutMapping("/profile")
	public ResponseEntity<ApiResponse<UserResponse>> updateUserProfile(@RequestHeader("X-User-Email") String email,
	                                                                   @RequestBody UpdateRequest updateRequest) {
		return ResponseEntity.ok(ApiResponse.success(userService.updateUser(email, updateRequest), 200));
	}

	@GetMapping("/{userId}")
	public ResponseEntity<ApiResponse<UserResponse>> getUserById(@PathVariable String userId) {
		return ResponseEntity.ok(ApiResponse.success(userService.getUserById(Long.valueOf(userId)), 200));
	}

	@GetMapping("/admin/users")
	@PreAuthorize("hasRole('ADMIN') AND hasAuthority('VIEW_ALL_USERS')")
	public ResponseEntity<ApiResponse<List<UserResponse>>> getAllUsers() {
		return ResponseEntity.ok(ApiResponse.success(userService.getAllUsers(), 200));
	}

//	@PatchMapping("/admin/users/{userId}/suspend")
//	@PreAuthorize("hasRole('ADMIN') AND hasAuthority('SUSPEND_USER')")
//	public ResponseEntity<ApiResponse<UserResponse>> suspendUser(@PathVariable String userId) {
//		return ResponseEntity.ok(ApiResponse.success(userService.suspendUser(Long.valueOf(userId)), 200));
//	}
//
//	@PatchMapping("/admin/users/{userId}/activate")
//	@PreAuthorize("hasRole('ADMIN') AND hasAuthority('ACTIVATE_USER')")
//	public ResponseEntity<ApiResponse<UserResponse>> activateUser(@PathVariable String userId) {
//		return ResponseEntity.ok(ApiResponse.success(userService.activateUser(Long.valueOf(userId)), 200));
//	}
//
//	@DeleteMapping("/admin/users/{userId}")
//	@PreAuthorize("hasRole('ADMIN') AND hasAuthority('DELETE_USER')")
//	public ResponseEntity<ApiResponse<String>> deleteUser(@PathVariable String userId) {
//		userService.deleteUser(Long.valueOf(userId));
//		return ResponseEntity.ok(ApiResponse.success("User deleted successfully", 200));
//	}
}
