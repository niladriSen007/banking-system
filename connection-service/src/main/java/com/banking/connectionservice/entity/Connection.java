package com.banking.connectionservice.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Builder
@RequiredArgsConstructor
@Table(name="connection")
public class Connection {

	@Id
	@GeneratedValue(strategy = GenerationType.UUID)
	private String id;

	@Column(nullable = false)
	@NotBlank(message = "Follower id must be present")
	private String followerId; // who is following

	@Column(nullable = false)
	@NotBlank(message = "Followee id must be present")
	private String followeeId; // who is being followed

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private ConnectionStatus connectionStatus;

	@CreationTimestamp
	private LocalDateTime createdDate;

	@UpdateTimestamp
	private LocalDateTime updatedDate;

}
