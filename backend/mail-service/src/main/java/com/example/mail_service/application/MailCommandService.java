package com.example.mail_service.application;

import com.example.mail_service.api.dto.MailSendRequest;
import com.example.mail_service.api.dto.MailSendResponse;
import com.example.mail_service.common.MailEvent;
import com.example.mail_service.domain.MailRequest;
import com.example.mail_service.domain.MailRequestRepository;
import com.example.mail_service.infrastructure.redis.MailRateLimitService;
import com.example.mail_service.outbox.OutboxEvent;
import com.example.mail_service.outbox.OutboxEventRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class MailCommandService {

    private final MailRequestRepository mailRequestRepository;
    private final OutboxEventRepository outboxEventRepository;
    private final MailRateLimitService rateLimitService;
    private final ObjectMapper objectMapper;

    @Transactional
    public MailSendResponse sendMail(MailSendRequest request) {
        if (!rateLimitService.isAllowed(request.recipientEmail())) {
            throw new IllegalStateException("Rate limit exceeded for: " + request.recipientEmail());
        }

        MailRequest mailRequest = MailRequest.of(
                request.recipientEmail(),
                request.subject(),
                request.body()
        );
        mailRequestRepository.save(mailRequest);

        String payload = toJson(new MailEvent(
                mailRequest.getId(),
                mailRequest.getRecipientEmail(),
                mailRequest.getSubject(),
                mailRequest.getBody()
        ));

        OutboxEvent outboxEvent = OutboxEvent.of(
                mailRequest.getId(),
                "MAIL_REQUESTED",
                payload
        );
        outboxEventRepository.save(outboxEvent);

        log.info("Mail request saved. id={}, recipient={}", mailRequest.getId(), mailRequest.getRecipientEmail());

        return new MailSendResponse(mailRequest.getId(), mailRequest.getStatus().name());
    }

    private String toJson(Object object) {
        try {
            return objectMapper.writeValueAsString(object);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to serialize event payload", e);
        }
    }
}
