package com.pivo.weev.backend.domain.service.message;

import com.pivo.weev.backend.domain.model.messaging.MailMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MailMessagingService {

    private final MailSender mailSender;
    private final MailMessageFactory mailMessageFactory;

    public void sendVerificationMessage(String email, String verificationCode) {
        MailMessage message = mailMessageFactory.buildEmailVerificationMessage(email, verificationCode);
        mailSender.sendHtmlMessage(message);
    }
}
