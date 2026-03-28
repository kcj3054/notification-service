package com.example.mail_service.domain;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MailRequestRepository extends JpaRepository<MailRequest, String> {

    List<MailRequest> findByRecipientEmail(String recipientEmail);
}
