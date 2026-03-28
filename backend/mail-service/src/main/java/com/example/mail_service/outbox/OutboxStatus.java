package com.example.mail_service.outbox;

public enum OutboxStatus {
    PENDING,
    PUBLISHED,
    FAILED
}
