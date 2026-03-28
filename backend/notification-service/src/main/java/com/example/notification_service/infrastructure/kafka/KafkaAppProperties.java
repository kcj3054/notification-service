package com.example.notification_service.infrastructure.kafka;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app.kafka")
public record KafkaAppProperties(String topic, String dlqTopic) {

}
