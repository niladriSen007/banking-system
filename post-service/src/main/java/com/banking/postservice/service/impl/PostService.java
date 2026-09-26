package com.banking.postservice.service.impl;

import com.banking.postservice.dto.CreatePostRequest;
import com.banking.postservice.dto.PostResponse;
import com.banking.postservice.entity.*;
import com.banking.postservice.event.PostCreatedEvent;
import com.banking.postservice.exception.PostNotFoundException;
import com.banking.postservice.repository.OutboxEventRepository;
import com.banking.postservice.repository.PostByIdRepository;
import com.banking.postservice.repository.PostByUserRepository;
import com.banking.postservice.repository.RecentPostRepository;
import com.banking.postservice.service.IPostService;
import com.fasterxml.jackson.core.JsonProcessingException;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import tools.jackson.databind.ObjectMapper;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PostService implements IPostService {

	private final PostByIdRepository postByIdRepository;
	private final PostByUserRepository postByUserRepository;
	private final RecentPostRepository recentPostRepository;
	private final OutboxEventRepository outboxEventRepository;
	private final ObjectMapper objectMapper;

	@Override
	public PostResponse createPost(
			CreatePostRequest request
	) throws JsonProcessingException {

		UUID postId = UUID.randomUUID();

		UUID eventId = UUID.randomUUID();

		UUID userId = UUID.randomUUID();

		LocalDateTime now = LocalDateTime.now();

		LocalDate bucketDate =
				now.atZone(
						java.time.ZoneOffset.UTC
				).toLocalDate();



		// =====================================================
		// 1. posts_by_id
		// =====================================================

		PostById postById =
				PostById.builder()
						.postId(postId)
						.userId(userId)
						.content(request.content())
						.createdAt(now)
						.updatedAt(now)
						.likeCount(0L)
						.commentCount(0L)
						.build();

		postByIdRepository.save(postById);


		// =====================================================
		// 2. posts_by_user
		// =====================================================

		PostByUserKey userKey =
				PostByUserKey.builder()
						.userId(userId)
						.createdAt(now)
						.postId(postId)
						.build();

		PostByUser postByUser =
				PostByUser.builder()
						.key(userKey)
						.content(request.content())
						.updatedAt(now)
						.likeCount(0L)
						.commentCount(0L)
						.build();

		postByUserRepository.save(postByUser);


		// =====================================================
		// 3. recent_posts
		// =====================================================

		RecentPostKey recentKey =
				RecentPostKey.builder()
						.bucketDate(bucketDate)
						.createdAt(now)
						.postId(postId)
						.build();

		RecentPost recentPost =
				RecentPost.builder()
						.key(recentKey)
						.userId(userId)
						.content(request.content())
						.updatedAt(now)
						.likeCount(0L)
						.commentCount(0L)
						.build();

		recentPostRepository.save(recentPost);


		// =====================================================
		// 4. Create Kafka event
		// =====================================================

		PostCreatedEvent event =
				new PostCreatedEvent(
						eventId,
						postId,
						userId,
						request.content(),
						now
				);


		String payload =
				objectMapper.writeValueAsString(event);


		// =====================================================
		// 5. Save OUTBOX
		// =====================================================

		OutboxEventKey outboxKey =
				OutboxEventKey.builder()
						.bucketDate(bucketDate)
						.eventTime(now)
						.eventId(eventId)
						.build();

		OutboxEvent outbox =
				OutboxEvent.builder()
						.key(outboxKey)
						.aggregateId(postId)
						.eventType("post.created")
						.payload(payload)
						.status("NEW")
						.retryCount(0)
						.build();

		outboxEventRepository.save(outbox);


		return new PostResponse(
				postId,
				userId,
				request.content(),
				now,
				now,
				0L,
				0L
		);
	}


	@Override
	public PostResponse getPost(UUID postId) {

		PostById post =
				postByIdRepository
						.findById(postId)
						.orElseThrow(
								() -> new PostNotFoundException(
										"Post not found: " + postId
								)
						);

		return new PostResponse(
				post.getPostId(),
				post.getUserId(),
				post.getContent(),
				post.getCreatedAt(),
				post.getUpdatedAt(),
				post.getLikeCount(),
				post.getCommentCount()
		);
	}


	@Override
	public List<PostResponse> getPostsByUser(
			UUID userId
	) {

		return postByUserRepository
				.findByKeyUserId(userId)
				.stream()
				.map(post -> {

					PostByUserKey key =
							post.getKey();

					return new PostResponse(
							key.getPostId(),
							key.getUserId(),
							post.getContent(),
							key.getCreatedAt(),
							post.getUpdatedAt(),
							post.getLikeCount(),
							post.getCommentCount()
					);
				})
				.toList();
	}



	@Override
	public List<PostResponse> getRecentPosts() {

		LocalDate today =
				LocalDate.now(
						java.time.ZoneOffset.UTC
				);

		return recentPostRepository
				.findByKeyBucketDate(today)
				.stream()
				.map(post -> {

					RecentPostKey key =
							post.getKey();

					return new PostResponse(
							key.getPostId(),
							post.getUserId(),
							post.getContent(),
							key.getCreatedAt(),
							post.getUpdatedAt(),
							post.getLikeCount(),
							post.getCommentCount()
					);
				})
				.toList();
	}
}