package com.pivo.weev.backend.domain.service.message;

import com.pivo.weev.backend.domain.model.messaging.MailMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MessagingService {

    private final MailService mailService;
    private final MailMessageFactory mailMessageFactory;

    public void sendEmailVerificationMessage(String email, String verificationCode) {
        MailMessage message = mailMessageFactory.buildEmailVerificationMessage(email, verificationCode);
        mailService.sendHtmlMessage(message);
    }
}
