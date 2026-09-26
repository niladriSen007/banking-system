package com.banking.postservice.repository;

import com.banking.postservice.entity.PostByUser;
import com.banking.postservice.entity.PostByUserKey;
import org.springframework.data.cassandra.repository.CassandraRepository;

import java.util.List;
import java.util.UUID;

public interface PostByUserRepository
		extends CassandraRepository<PostByUser, PostByUserKey> {

	List<PostByUser> findByKeyUserId(
			UUID userId
	);
}