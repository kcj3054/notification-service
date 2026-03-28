package com.example.mail_service.outbox;

import com.example.mail_service.application.OutboxPublishService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class OutboxPollingPublisher {

    private final OutboxPublishService outboxPublishService;

    @Scheduled(fixedDelayString = "${app.outbox.polling-interval-ms:5000}")
    public void poll() {
        log.debug("Polling outbox for pending events...");
        outboxPublishService.publishPendingEvents();
    }
}
