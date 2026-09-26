package com.banking.postservice.repository;

import com.banking.postservice.entity.PostById;
import org.springframework.data.cassandra.repository.CassandraRepository;

import java.util.UUID;

public interface PostByIdRepository
		extends CassandraRepository<PostById, UUID> {
}