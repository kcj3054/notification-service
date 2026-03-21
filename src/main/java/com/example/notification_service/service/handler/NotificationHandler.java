package com.example.notification_service.service.handler;

import com.example.notification_service.domain.NotificationType;
import com.example.notification_service.event.NotificationEvent;

public interface NotificationHandler {

    NotificationType supports();
    void handle(NotificationEvent event);
}
