package com.example.mail_service.domain;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "mail_requests")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MailRequest {

    @Id
    @Column(columnDefinition = "VARCHAR(36)")
    private String id;

    @Column(nullable = false)
    private String recipientEmail;

    @Column(nullable = false)
    private String subject;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String body;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MailRequestStatus status;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        this.id = UUID.randomUUID().toString();
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        this.status = MailRequestStatus.PENDING;
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    public static MailRequest of(String recipientEmail, String subject, String body) {
        MailRequest request = new MailRequest();
        request.recipientEmail = recipientEmail;
        request.subject = subject;
        request.body = body;
        return request;
    }

    public void updateStatus(MailRequestStatus status) {
        this.status = status;
    }
}
