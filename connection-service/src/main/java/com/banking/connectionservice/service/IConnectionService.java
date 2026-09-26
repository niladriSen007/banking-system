package com.banking.connectionservice.service;

import java.util.List;

public interface IConnectionService {
	List<?> getConnections(String currentUserId);
	String sendConnectionRequest(String targetUserId,String currentUserId);
	String acceptConnectionRequest(String connectionId,String currentUserId);
}
