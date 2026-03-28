package com.example.notification_service.application.handler;

import com.example.notification_service.domain.NotificationType;
import com.example.notification_service.common.NotificationEvent;

public interface NotificationHandler {

    NotificationType supports();
    void handle(NotificationEvent event);
}
