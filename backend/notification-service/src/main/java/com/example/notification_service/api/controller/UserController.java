package com.example.notification_service.api.controller;

import com.example.notification_service.infrastructure.kafka.NotificationEventProducer;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class UserController {
    private final NotificationEventProducer notificationEventProducer;

}
