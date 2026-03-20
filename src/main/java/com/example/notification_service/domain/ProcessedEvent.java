package com.example.notification_service.domain;


import jakarta.persistence.*;
import lombok.Generated;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CollectionIdJavaClass;

import java.time.LocalDateTime;

@NoArgsConstructor
@Table
@Entity
public class ProcessedEvent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "event_id")
    private String eventId;

    @Column(name = "local_date_time")
    private LocalDateTime processedAt;
}
