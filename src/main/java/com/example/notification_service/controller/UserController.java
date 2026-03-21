package com.example.notification_service.controller;

import com.example.notification_service.producer.NotificationEventProducer;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class UserController {
    private final NotificationEventProducer notificationEventProducer;

}
