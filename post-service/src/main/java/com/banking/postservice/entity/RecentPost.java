package com.banking.postservice.entity;

import lombok.*;
import org.springframework.data.cassandra.core.mapping.*;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table("recent_posts")
public class RecentPost {

	@PrimaryKey
	private RecentPostKey key;

	@Column("user_id")
	private UUID userId;

	private String content;

	@Column("updated_at")
	private LocalDateTime updatedAt;

	@Column("like_count")
	private Long likeCount;

	@Column("comment_count")
	private Long commentCount;
}