package com.pivo.weev.backend.domain.service.message;

import com.pivo.weev.backend.domain.model.auth.VerificationScope;
import com.pivo.weev.backend.domain.model.messaging.MailMessage;
import com.pivo.weev.backend.domain.model.messaging.source.ChangePasswordSource;
import com.pivo.weev.backend.domain.model.messaging.source.EmailVerificationSource;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MailMessagingService {

    private final MailSender mailSender;
    private final MailMessageFactory mailMessageFactory;

    @Async(value = "mailExecutor")
    public void sendVerificationMessage(String email, VerificationScope verificationScope, EmailVerificationSource source) {
        MailMessage message = mailMessageFactory.buildVerificationMessage(email, verificationScope, source);
        mailSender.sendHtmlMessage(message);
    }

    @Async(value = "mailExecutor")
    public void sendChangePasswordMessage(String email, ChangePasswordSource source) {
        MailMessage message = mailMessageFactory.buildChangePasswordMessage(email, source);
        mailSender.sendHtmlMessage(message);
    }
}
