package com.pivo.weev.backend.domain.service.message;

import static com.pivo.weev.backend.domain.utils.Constants.Messaging.Templates.VERIFICATION_TEMPLATES_MAPPING;

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
        String template = VERIFICATION_TEMPLATES_MAPPING.get(verificationScope);
        MailMessage message = mailMessageFactory.buildVerificationMessage(email, template, source);
        mailSender.sendHtmlMessage(message);
    }

    @Async(value = "mailExecutor")
    public void sendChangePasswordMessage(String email, ChangePasswordSource source) {
        MailMessage message = mailMessageFactory.buildChangePasswordMessage(email, source);
        mailSender.sendHtmlMessage(message);
    }
}
