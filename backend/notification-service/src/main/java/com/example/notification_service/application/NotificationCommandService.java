package com.example.notification_service.application;

import com.example.notification_service.domain.NotificationType;
import com.example.notification_service.common.NotificationEvent;
import com.example.notification_service.application.handler.NotificationHandler;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

//cpp의 dispacher, packet handler
@Service
public class NotificationCommandService {

    private final Map<NotificationType, NotificationHandler> handlerMap;

//    required와 생성자 같이 사용하면 충돌 남
    public NotificationCommandService(List<NotificationHandler> handlerList) {
        this.handlerMap = handlerList.stream()
                .collect(Collectors.toMap(NotificationHandler::supports, h -> h));
    }

//    valueOf
    public void handle(NotificationEvent event) {
        handlerMap.get(NotificationType.valueOf(event.eventType())).handle(event);
    }
}
