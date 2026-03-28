package com.example.notification_service.application.handler;

import com.example.notification_service.domain.Notifiaction;
import com.example.notification_service.domain.NotificationType;
import com.example.notification_service.domain.ProcessedEvent;
import com.example.notification_service.domain.NotificationRepository;
import com.example.notification_service.domain.ProcessedEventRepository;
import com.example.notification_service.common.NotificationEvent;
import com.example.notification_service.application.SseEmitterService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.LocalDateTime;

@Slf4j
@Component
@RequiredArgsConstructor
public class CommentNotificationHandler implements NotificationHandler {

    @Value("${spring.kafka.consumer.group-id}")
    private String consumerGroup;

    private final ProcessedEventRepository processedEventRepository;
    private final NotificationRepository notificationRepository;
    private final SseEmitterService sseEmitterService;

    @Override
    public NotificationType supports() {
        return NotificationType.COMMENT_CREATED;
    }

    @Override
    public void handle(NotificationEvent event) {

        // 중복 체크
        if (processedEventRepository.existsByConsumerGroupAndEventId(consumerGroup, event.eventId())) {
            log.warn("Duplicate event detected. eventId={}", event.eventId());
            return;
        }

        // processedEvent 저장
        processedEventRepository.save(ProcessedEvent.builder()
                .consumerGroup(consumerGroup)
                .eventId(event.eventId())
                .processedAt(LocalDateTime.now())
                .build());

        // notification 저장
        notificationRepository.save(Notifiaction.builder()
                .eventId(event.eventId())
                .receivedUserId(event.receivedUserId())
                .sendUserId(event.actorUserId())
                .createdAt(Instant.now())
                .build());

        // SSE push
        sseEmitterService.send(event.receivedUserId(), event);
    }
}
