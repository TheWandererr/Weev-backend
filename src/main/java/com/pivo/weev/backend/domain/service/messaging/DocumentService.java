package com.pivo.weev.backend.domain.service.messaging;

import static org.mapstruct.factory.Mappers.getMapper;

import com.pivo.weev.backend.domain.mapping.domain.EmailVerificationTemplateModelMapper;
import com.pivo.weev.backend.domain.model.auth.VerificationScope;
import com.pivo.weev.backend.domain.model.messaging.mail.MailMessage;
import com.pivo.weev.backend.domain.model.messaging.template.ChangePasswordTemplateModel;
import com.pivo.weev.backend.domain.model.messaging.template.EmailVerificationTemplateModel;
import com.pivo.weev.backend.domain.model.user.User;
import com.pivo.weev.backend.domain.service.messaging.factory.MailMessageFactory;
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
        EmailVerificationTemplateModel model = getMapper(EmailVerificationTemplateModelMapper.class).map(recipient, verificationCode);
        MailMessage message = mailMessageFactory.buildVerificationMessage(email, verificationScope, model);
        mailSender.sendHtmlMessage(message);
    }

    @Async(value = "mailExecutor")
    public void sendChangePasswordMail(String email, String nickname) {
        ChangePasswordTemplateModel model = new ChangePasswordTemplateModel(nickname);
        MailMessage message = mailMessageFactory.buildChangePasswordMessage(email, model);
        mailSender.sendHtmlMessage(message);
    }
}
