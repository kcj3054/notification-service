package com.example.notification_service.repository;

import com.example.notification_service.domain.ProcessedEvent;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProcessedEventRepository extends JpaRepository<ProcessedEvent, Long> {

    boolean existsByConsumerNameAndEventId(String consumerName, Long eventId);
}
