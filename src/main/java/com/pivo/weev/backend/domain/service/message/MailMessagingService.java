package com.pivo.weev.backend.domain.service.message;

import com.pivo.weev.backend.domain.model.messaging.MailMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MailMessagingService {

    private final MailSender mailSender;
    private final MailMessageFactory mailMessageFactory;

    @Async(value = "mailExecutor")
    public void sendVerificationMessage(String email, String verificationCode) {
        MailMessage message = mailMessageFactory.buildVerificationMessage(email, verificationCode);
        mailSender.sendHtmlMessage(message);
    }

    @Async(value = "mailExecutor")
    public void sendChangePasswordMessage(String nickname, String email) {
        MailMessage message = mailMessageFactory.buildChangePasswordMessage(nickname, email);
        mailSender.sendHtmlMessage(message);
    }
}
