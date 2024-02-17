package com.pivo.weev.backend.domain.service.message;

import static org.mapstruct.factory.Mappers.getMapper;

import com.pivo.weev.backend.domain.mapping.domain.EmailVerificationSourceMapper;
import com.pivo.weev.backend.domain.model.auth.VerificationScope;
import com.pivo.weev.backend.domain.model.messaging.mail.MailMessage;
import com.pivo.weev.backend.domain.model.messaging.source.ChangePasswordSource;
import com.pivo.weev.backend.domain.model.messaging.source.EmailVerificationSource;
import com.pivo.weev.backend.domain.model.user.User;
import com.pivo.weev.backend.domain.service.message.factory.MailMessageFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DocumentService {

    private final MailSender mailSender;
    private final MailMessageFactory mailMessageFactory;

    @Async(value = "mailExecutor")
    public void sendVerificationMail(String email, VerificationScope verificationScope, User recipient, String verificationCode) {
        EmailVerificationSource source = getMapper(EmailVerificationSourceMapper.class).map(recipient, verificationCode);
        MailMessage message = mailMessageFactory.buildVerificationMessage(email, verificationScope, source);
        mailSender.sendHtmlMessage(message);
    }

    @Async(value = "mailExecutor")
    public void sendChangePasswordMail(String email, String nickname) {
        ChangePasswordSource source = new ChangePasswordSource(nickname);
        MailMessage message = mailMessageFactory.buildChangePasswordMessage(email, source);
        mailSender.sendHtmlMessage(message);
    }
}
