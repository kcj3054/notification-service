package com.example.notification_service.event;

public record NotificationEvent(String eventId, String eventType, Long actorUserId, Long receivedUserId,
                                Long targetId,
                                String targetType,
                                String title, String content) {
}
