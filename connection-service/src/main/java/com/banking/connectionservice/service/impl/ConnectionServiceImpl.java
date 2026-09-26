package com.banking.connectionservice.service.impl;

import com.banking.connectionservice.constants.Topic;
import com.banking.connectionservice.entity.Connection;
import com.banking.connectionservice.entity.ConnectionStatus;
import com.banking.connectionservice.entity.OutboxEvent;
import com.banking.connectionservice.event.ConnectionRequestAcceptedEvent;
import com.banking.connectionservice.event.ConnectionRequestSentEvent;
import com.banking.connectionservice.exception.ConnectionRequestAlreadyExists;
import com.banking.connectionservice.exception.ConnectionRequestDoesNotExist;
import com.banking.connectionservice.exception.NotAuthorisedToAcceptConnectionRequest;
import com.banking.connectionservice.repository.ConnectionRepository;
import com.banking.connectionservice.repository.OutboxRepository;
import com.banking.connectionservice.service.IConnectionService;
import tools.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class ConnectionServiceImpl implements IConnectionService {

	private final ConnectionRepository connectionRepository;
	private final OutboxRepository outboxEventRepository;
	private final ObjectMapper objectMapper;

	@Override
	public List<Connection> getConnections(String currentUserId) {
		List<Connection> userConnections = connectionRepository.findUserConnections(currentUserId, ConnectionStatus.ACCEPTED);
		return userConnections;
	}

	@Override
	public String sendConnectionRequest(String targetUserId, String currentUserId) {
		if (connectionRepository.existsByFollowerIdAndFolloweeId(currentUserId, targetUserId)) {
			throw new ConnectionRequestAlreadyExists("Connection request already exists");
		}

		Connection connectionRequest = Connection.builder()
				.followerId(currentUserId)
				.followeeId(targetUserId)
				.connectionStatus(ConnectionStatus.PENDING)
				.build();

		Connection savedConnection = connectionRepository.save(connectionRequest);

		ConnectionRequestSentEvent event = ConnectionRequestSentEvent.builder()
				.connectionId(savedConnection.getId())
				.followerId(currentUserId)
				.followeeId(targetUserId)
				.connectionStatus(ConnectionStatus.PENDING)
				.build();

		// NOTIFICATION SERVICE will consume this event
		publishEvent(Topic.CONNECTION_REQUEST_SENT_TOPIC, savedConnection.getId(), event);
		return savedConnection.getId();
	}

	@Override
	public String acceptConnectionRequest(String connectionId, String currentUserId) {

		log.info("Accept connection - Service layer");
		Connection connectionRequest = connectionRepository.findById(connectionId)
				.orElseThrow(() -> new ConnectionRequestDoesNotExist("Connection request is invalid"));
		if (!currentUserId.equals(connectionRequest.getFolloweeId())) {
			throw new NotAuthorisedToAcceptConnectionRequest("You are not the correct user to accept the request");
		}
		connectionRequest.setConnectionStatus(ConnectionStatus.ACCEPTED);
		connectionRepository.save(connectionRequest);
		log.info("Connection request accepted");

		ConnectionRequestAcceptedEvent event = ConnectionRequestAcceptedEvent.builder()
				.connectionId(connectionRequest.getId())
				.followerId(currentUserId)
				.followeeId(connectionRequest.getFolloweeId())
				.connectionStatus(ConnectionStatus.ACCEPTED)
				.build();

		publishEvent(Topic.CONNECTION_REQUEST_ACCEPTED_TOPIC, connectionRequest.getId(), event);
		log.info("Connection request accepted event pushed to {} topic", Topic.CONNECTION_REQUEST_ACCEPTED_TOPIC);
		return "Connection accepted";
	}


	/**
	 * Outbox pattern is used to ensure that the event is published only after the transaction is committed successfully.
	 * The event is stored in the outbox table and then published to the Kafka topic.
	 * The outbox table is then cleaned up by a scheduled job.
	 * This ensures that the event is published only after the transaction is committed successfully.
	 * The outbox table is then cleaned up by a scheduled job.
	 */
	private void publishEvent(String topic, String connectionId, Object event) {
		try {
			String payload = objectMapper.writeValueAsString(event);

			OutboxEvent outboxEvent = OutboxEvent.builder()
					.id(UUID.randomUUID())
					.aggregateType("connection")
					.aggregateId(connectionId)
					.type(topic)
					.payload(payload)
					.build();

			outboxEventRepository.save(outboxEvent);
		} catch (Exception e) {
			log.error("Failed to serialize Connection request sent event", e);
			throw new IllegalStateException(
					"Failed to serialize Connection request sent event event",
					e
			);
		}
	}
}
