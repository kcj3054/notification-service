package com.example.notification_service.api.dto;

public record NotificationRequest(
        String eventType,
        Long actorUserId,
        Long receivedUserId,
        Long targetId,
        String targetType,
        String title,
        String content
) {
}
