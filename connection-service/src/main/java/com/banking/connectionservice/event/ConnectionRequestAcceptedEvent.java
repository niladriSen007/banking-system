package com.banking.connectionservice.event;

import com.banking.connectionservice.entity.ConnectionStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ConnectionRequestAcceptedEvent {
	private String connectionId;
	private String followerId;
	private String followeeId;
	private ConnectionStatus connectionStatus;
}
