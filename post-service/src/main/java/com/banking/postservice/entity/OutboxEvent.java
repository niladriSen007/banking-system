package com.banking.postservice.entity;

import lombok.*;
import org.springframework.data.cassandra.core.mapping.*;

import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table("outbox_events")
public class OutboxEvent {

	@PrimaryKey
	private OutboxEventKey key;

	@Column("aggregateid")
	private UUID aggregateId;

	@Column("eventtype")
	private String eventType;

	private String payload;

	private String status;

	@Column("retrycount")
	private Integer retryCount;

	@Column("lastattemptat")
	private Instant lastAttemptAt;

	@Column("publishedat")
	private Instant publishedAt;

	@Column("lasterror")
	private String lastError;
}