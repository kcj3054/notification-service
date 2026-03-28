package com.example.mail_service.api.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record MailSendRequest(
        @NotBlank @Email String recipientEmail,
        @NotBlank String subject,
        @NotBlank String body
) {}
