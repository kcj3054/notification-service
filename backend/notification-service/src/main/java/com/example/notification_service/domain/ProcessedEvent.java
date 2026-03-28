package com.example.notification_service.domain;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table
@Entity
public class ProcessedEvent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "consumer_group")
    private String consumerGroup;

    @Column(name = "event_id")
    private String eventId;

    @Column(name = "processed_at")
    private LocalDateTime processedAt;
}
