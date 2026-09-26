package com.banking.connectionservice.repository;

import com.banking.connectionservice.entity.Connection;
import com.banking.connectionservice.entity.ConnectionStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ConnectionRepository extends JpaRepository<Connection, String> {
	boolean existsByFollowerIdAndFolloweeId(String currentUserId, String targetUserId);

	@Query("""
				SELECT c FROM Connection c
				WHERE (c.followerId = :currentUserId
				OR c.followeeId = :currentUserId)
				AND c.connectionStatus = :status
			""")
	List<Connection> findUserConnections(@Param("currentUserId") String currentUserId,@Param("status") ConnectionStatus status);
}
