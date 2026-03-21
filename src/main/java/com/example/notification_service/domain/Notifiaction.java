package com.example.notification_service.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.time.LocalDateTime;

@Entity
@Table (
        name="notification",
        uniqueConstraints = {
                @UniqueConstraint(name = "uq_notification_id", columnNames = "event_id")
        }
)
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
public class Notifiaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "event_id", nullable = false, length = 20)
    private String eventId;

    @Column(name = "received_user_id", nullable = false)
    private Long receivedUserId;

    @Column(name = "send_user_id", nullable = false)
    private Long sendUserId;

    @Column(name = "read_at")
    private Instant readAt;

    @Column(name= "create_at")
    private Instant createdAt;


}
