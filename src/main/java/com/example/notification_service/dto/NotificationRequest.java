package com.example.notification_service.dto;

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
