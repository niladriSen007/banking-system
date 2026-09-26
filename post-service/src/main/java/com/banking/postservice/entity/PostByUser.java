package com.banking.postservice.entity;

import lombok.*;
import org.springframework.data.cassandra.core.mapping.Column;
import org.springframework.data.cassandra.core.mapping.PrimaryKey;
import org.springframework.data.cassandra.core.mapping.Table;

import java.time.Instant;
import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table("posts_by_user")
public class PostByUser {

	@PrimaryKey
	private PostByUserKey key;

	private String content;

	@Column("updated_at")
	private LocalDateTime updatedAt;

	@Column("like_count")
	private Long likeCount;

	@Column("comment_count")
	private Long commentCount;
}