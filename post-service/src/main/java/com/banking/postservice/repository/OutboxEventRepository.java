package com.banking.postservice.repository;

import com.banking.postservice.entity.OutboxEvent;
import com.banking.postservice.entity.OutboxEventKey;
import org.springframework.data.cassandra.repository.CassandraRepository;

import java.time.LocalDate;
import java.util.List;

public interface OutboxEventRepository
		extends CassandraRepository<OutboxEvent, OutboxEventKey> {

	List<OutboxEvent> findByKeyBucketDateAndStatus(
			LocalDate bucketDate,
			String status
	);
}