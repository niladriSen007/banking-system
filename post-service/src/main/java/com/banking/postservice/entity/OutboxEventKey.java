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
public class OutboxEventKey implements Serializable {

	@PrimaryKeyColumn(
			name = "bucketdate",
			type = PrimaryKeyType.PARTITIONED
	)
	private LocalDate bucketDate;

	@PrimaryKeyColumn(
			name = "eventtime",
			type = PrimaryKeyType.CLUSTERED
	)
	private LocalDateTime eventTime;

	@PrimaryKeyColumn(
			name = "eventid",
			type = PrimaryKeyType.CLUSTERED
	)
	private UUID eventId;
}