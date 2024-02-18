package com.pivo.weev.backend.domain.service.messaging.factory;

import static com.pivo.weev.backend.domain.utils.Constants.Messaging.EmailMessages.EMAIL_CHANGE_PASSWORD_SUBJECT;
import static com.pivo.weev.backend.domain.utils.Constants.Messaging.EmailMessages.EMAIL_VERIFICATION_REQUEST_SUBJECT;
import static com.pivo.weev.backend.domain.utils.Constants.Messaging.Templates.EMAIL_CHANGE_PASSWORD_FTL;
import static com.pivo.weev.backend.domain.utils.Constants.Messaging.Templates.VERIFICATION_TEMPLATES_MAPPING;

import com.pivo.weev.backend.config.messaging.MessageBundle;
import com.pivo.weev.backend.config.messaging.TemplatesBundle;
import com.pivo.weev.backend.domain.model.auth.VerificationScope;
import com.pivo.weev.backend.domain.model.messaging.mail.MailMessage;
import com.pivo.weev.backend.domain.model.messaging.source.ChangePasswordSource;
import com.pivo.weev.backend.domain.model.messaging.source.EmailVerificationSource;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class MailMessageFactory {

    private final MessageBundle messageBundle;
    private final TemplatesBundle templatesBundle;

    public MailMessage buildVerificationMessage(String email, VerificationScope verificationScope, EmailVerificationSource source) {
        MailMessage message = new MailMessage();
        String subject = messageBundle.getEmailMessage(EMAIL_VERIFICATION_REQUEST_SUBJECT);
        message.setSubject(subject);
        String template = VERIFICATION_TEMPLATES_MAPPING.get(verificationScope);
        String content = templatesBundle.getTemplateContent(template, source);
        message.setContent(content);
        message.setRecipient(email);
        return message;
    }

    public MailMessage buildChangePasswordMessage(String email, ChangePasswordSource source) {
        MailMessage message = new MailMessage();
        String subject = messageBundle.getEmailMessage(EMAIL_CHANGE_PASSWORD_SUBJECT);
        message.setSubject(subject);
        String content = templatesBundle.getTemplateContent(EMAIL_CHANGE_PASSWORD_FTL, source);
        message.setContent(content);
        message.setRecipient(email);
        return message;
    }
}
