package com.example.notification_service.consumer;

import com.example.notification_service.config.KafkaAppProperties;
import com.example.notification_service.event.NotificationEvent;
import com.example.notification_service.service.NotificationCommandService;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.annotation.TopicPartition;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class NotificationEventConsumer {
    private final NotificationCommandService notificationCommandService;

    @KafkaListener(
            topics = "${app.kafka.topic}",
            groupId = "${spring.kafka.consumer.group-id}"
    )
    public void consume(NotificationEvent event) {
        notificationCommandService.handle(event);
    }
}
