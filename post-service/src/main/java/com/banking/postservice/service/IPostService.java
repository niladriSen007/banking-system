package com.banking.postservice.service;

import com.banking.postservice.dto.CreatePostRequest;
import com.banking.postservice.dto.PostResponse;
import com.fasterxml.jackson.core.JsonProcessingException;
import jakarta.validation.Valid;

import java.util.List;
import java.util.UUID;

public interface IPostService {
	PostResponse createPost(@Valid CreatePostRequest request) throws JsonProcessingException;

	PostResponse getPost(UUID postId);

	List<PostResponse> getPostsByUser(UUID userId);

	List<PostResponse> getRecentPosts();
}
