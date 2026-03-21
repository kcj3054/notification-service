package com.example.notification_service.producer;


import com.example.notification_service.config.KafkaAppProperties;
import com.example.notification_service.event.NotificationEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class NotificationEventProducer {

    private final KafkaTemplate<String, NotificationEvent> kafkaTemplate;
    private final KafkaAppProperties kafkaAppProperties;

    public void send(NotificationEvent event) {
        String key = String.valueOf(event.eventId());
        kafkaTemplate.send(kafkaAppProperties.topic(), key, event);
    }
}
