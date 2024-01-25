package com.pivo.weev.backend.domain.service.message;

import static com.pivo.weev.backend.domain.utils.Constants.Messaging.EmailMessages.EMAIL_VERIFICATION_REQUEST_SUBJECT;
import static com.pivo.weev.backend.domain.utils.Constants.Messaging.Templates.EMAIL_VERIFICATION_REQUEST;

import com.pivo.weev.backend.config.messaging.MessageBundle;
import com.pivo.weev.backend.config.messaging.TemplatesBundle;
import com.pivo.weev.backend.domain.model.messaging.MailMessage;
import com.pivo.weev.backend.domain.model.messaging.source.EmailVerificationSource;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class MailMessageFactory {

    private final MessageBundle messageBundle;
    private final TemplatesBundle templatesBundle;

    public MailMessage buildEmailVerificationMessage(String email, String verificationCode) {
        MailMessage message = new MailMessage();
        String subject = messageBundle.getEmailMessage(EMAIL_VERIFICATION_REQUEST_SUBJECT);
        message.setSubject(subject);
        String content = templatesBundle.getTemplateContent(EMAIL_VERIFICATION_REQUEST, new EmailVerificationSource(verificationCode));
        message.setContent(content);
        message.setRecipient(email);
        return message;
    }
}
