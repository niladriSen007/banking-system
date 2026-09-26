package com.banking.connectionservice.controller;

import com.banking.connectionservice.dto.shared.ApiResponse;
import com.banking.connectionservice.service.IConnectionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/api/v1/connections")
public class ConnectionController {

	private final IConnectionService connectionService;

	@GetMapping
	public ResponseEntity<ApiResponse<List<?>>> getConnections(
			@RequestHeader("X-User-Id") String currentUserId
	){
		log.info("Transfer amount - Controller");
		return ResponseEntity.ok(
				ApiResponse.success(connectionService.getConnections(currentUserId), HttpStatus.CREATED.value())
		);
	}
}
