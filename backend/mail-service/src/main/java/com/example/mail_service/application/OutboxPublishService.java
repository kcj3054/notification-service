package com.example.mail_service.application;

import com.example.mail_service.domain.MailRequest;
import com.example.mail_service.domain.MailRequestRepository;
import com.example.mail_service.domain.MailRequestStatus;
import com.example.mail_service.outbox.OutboxEvent;
import com.example.mail_service.outbox.OutboxEventRepository;
import com.example.mail_service.outbox.OutboxStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class OutboxPublishService {

    private final OutboxEventRepository outboxEventRepository;
    private final MailRequestRepository mailRequestRepository;
    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Value("${app.kafka.topic}")
    private String topic;

    @Transactional
    public void publishPendingEvents() {
        List<OutboxEvent> pendingEvents = outboxEventRepository.findByStatus(OutboxStatus.PENDING);

        for (OutboxEvent event : pendingEvents) {
            try {
                kafkaTemplate.send(topic, event.getAggregateId(), event.getPayload()).get();
                event.markPublished();

                mailRequestRepository.findById(event.getAggregateId())
                        .ifPresent(mailRequest -> mailRequest.updateStatus(MailRequestStatus.SENT));

                log.info("Outbox event published. id={}, aggregateId={}", event.getId(), event.getAggregateId());
            } catch (Exception e) {
                event.markFailed();
                log.error("Failed to publish outbox event. id={}", event.getId(), e);
            }
        }
    }
}
