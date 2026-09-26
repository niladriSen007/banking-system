package com.banking.postservice.entity;

import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.cassandra.core.mapping.Column;
import org.springframework.data.cassandra.core.mapping.PrimaryKey;
import org.springframework.data.cassandra.core.mapping.Table;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table("posts_by_id")
public class PostById {

	@PrimaryKey
	@Column("post_id")
	private UUID postId;

	@Column("user_id")
	private UUID userId;

	@Column("content")
	private String content;

	@Column("created_at")
	private LocalDateTime createdAt;

	@Column("updated_at")
	private LocalDateTime updatedAt;

	@Column("like_count")
	private Long likeCount;

	@Column("comment_count")
	private Long commentCount;
}