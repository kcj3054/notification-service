package com.example.mail_service.api.dto;

public record MailSendResponse(
        String mailRequestId,
        String status
) {}
