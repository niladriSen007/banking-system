package com.banking.postservice.controller;

import com.banking.postservice.dto.CreatePostRequest;
import com.banking.postservice.dto.PostResponse;
import com.banking.postservice.service.IPostService;
import com.fasterxml.jackson.core.JsonProcessingException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/posts")
@RequiredArgsConstructor
public class PostController {

	private final IPostService postService;


	@PostMapping
	public ResponseEntity<PostResponse> createPost(
			@Valid
			@RequestBody
			CreatePostRequest request
	) throws JsonProcessingException {

		PostResponse response =
				postService.createPost(request);

		return ResponseEntity
				.status(HttpStatus.CREATED)
				.body(response);
	}


	@GetMapping("/{postId}")
	public ResponseEntity<PostResponse> getPost(
			@PathVariable UUID postId
	) {

		return ResponseEntity.ok(
				postService.getPost(postId)
		);
	}


	@GetMapping("/user/{userId}")
	public ResponseEntity<List<PostResponse>> getPostsByUser(
			@PathVariable UUID userId
	) {

		return ResponseEntity.ok(
				postService.getPostsByUser(userId)
		);
	}


	@GetMapping("/recent")
	public ResponseEntity<List<PostResponse>> getRecentPosts() {

		return ResponseEntity.ok(
				postService.getRecentPosts()
		);
	}
}