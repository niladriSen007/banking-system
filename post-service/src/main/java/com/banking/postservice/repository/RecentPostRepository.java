package com.banking.postservice.repository;

import com.banking.postservice.entity.RecentPost;
import com.banking.postservice.entity.RecentPostKey;
import org.springframework.data.cassandra.repository.CassandraRepository;

import java.time.LocalDate;
import java.util.List;

public interface RecentPostRepository
		extends CassandraRepository<RecentPost, RecentPostKey> {

	List<RecentPost> findByKeyBucketDate(
			LocalDate bucketDate
	);
}