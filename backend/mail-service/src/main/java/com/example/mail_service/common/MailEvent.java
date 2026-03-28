package com.example.mail_service.common;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class MailEvent {

    private String mailRequestId;
    private String recipientEmail;
    private String subject;
    private String body;
}
