package com.example.notification_service.infrastructure.kafka;

import com.example.notification_service.common.NotificationEvent;
import com.example.notification_service.application.NotificationCommandService;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
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
