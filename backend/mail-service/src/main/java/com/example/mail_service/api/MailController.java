package com.example.mail_service.api;

import com.example.mail_service.api.dto.MailSendRequest;
import com.example.mail_service.api.dto.MailSendResponse;
import com.example.mail_service.application.MailCommandService;
import com.example.mail_service.application.MailQueryService;
import com.example.mail_service.domain.MailRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/mails")
@RequiredArgsConstructor
public class MailController {

    private final MailCommandService mailCommandService;
    private final MailQueryService mailQueryService;

    @PostMapping
    public ResponseEntity<MailSendResponse> sendMail(@Valid @RequestBody MailSendRequest request) {
        MailSendResponse response = mailCommandService.sendMail(request);
        return ResponseEntity.accepted().body(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<MailRequest> getMailStatus(@PathVariable String id) {
        MailRequest mailRequest = mailQueryService.getMailRequest(id);
        return ResponseEntity.ok(mailRequest);
    }
}
