package com.example.notification_service.controller;

import com.example.notification_service.dto.NotificationRequest;
import com.example.notification_service.event.NotificationEvent;
import com.example.notification_service.producer.NotificationEventProducer;
import com.example.notification_service.service.SseEmitterService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/notification")
public class NotificationController {

    private final NotificationEventProducer notificationEventProducer;
    private final SseEmitterService sseEmitterService;

    @PostMapping
    public ResponseEntity<Void> sendNotification(@RequestBody NotificationRequest request) {
        NotificationEvent event = NotificationEvent.builder()
                .eventId(UUID.randomUUID().toString())
                .eventType(request.eventType())
                .actorUserId(request.actorUserId())
                .receivedUserId(request.receivedUserId())
                .targetId(request.targetId())
                .targetType(request.targetType())
                .title(request.title())
                .content(request.content())
                .build();
        notificationEventProducer.send(event);
        return ResponseEntity.ok().build();
    }

//    todo: userId를  path로 받는 것이 아니라, 인증정보에서 추출할 예정
    @GetMapping(value = "subscribe/{userId}", produces =MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter subscribe(@PathVariable Long userId) {
        return sseEmitterService.subscribe(userId);
    }
}
