package com.example.notification_service.domain;

import org.springframework.data.jpa.repository.JpaRepository;

public interface ProcessedEventRepository extends JpaRepository<ProcessedEvent, Long> {

    boolean existsByConsumerGroupAndEventId(String consumerGroup, String eventId);
}
