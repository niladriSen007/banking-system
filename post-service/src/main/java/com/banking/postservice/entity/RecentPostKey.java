package com.banking.postservice.entity;

import lombok.*;
import org.springframework.data.cassandra.core.cql.PrimaryKeyType;
import org.springframework.data.cassandra.core.mapping.Column;
import org.springframework.data.cassandra.core.mapping.PrimaryKeyClass;
import org.springframework.data.cassandra.core.mapping.PrimaryKeyColumn;

import java.io.Serializable;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@PrimaryKeyClass
public class RecentPostKey implements Serializable {

	@PrimaryKeyColumn(
			name = "bucket_date",
			type = PrimaryKeyType.PARTITIONED
	)
	private LocalDate bucketDate;

	@PrimaryKeyColumn(
			name = "created_at",
			type = PrimaryKeyType.CLUSTERED
	)
	private LocalDateTime createdAt;

	@PrimaryKeyColumn(
			name = "post_id",
			type = PrimaryKeyType.CLUSTERED
	)
	private UUID postId;
}