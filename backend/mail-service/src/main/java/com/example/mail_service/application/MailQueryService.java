package com.example.mail_service.application;

import com.example.mail_service.domain.MailRequest;
import com.example.mail_service.domain.MailRequestRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class MailQueryService {

    private final MailRequestRepository mailRequestRepository;

    @Transactional(readOnly = true)
    public MailRequest getMailRequest(String id) {
        return mailRequestRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("MailRequest not found: " + id));
    }
}
