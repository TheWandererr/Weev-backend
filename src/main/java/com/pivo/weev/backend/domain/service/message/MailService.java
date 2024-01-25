package com.pivo.weev.backend.domain.service.message;

import static org.mapstruct.factory.Mappers.getMapper;

import com.pivo.weev.backend.domain.mapping.domain.MimeMessageHelperMapper;
import com.pivo.weev.backend.domain.model.messaging.MailMessage;
import com.pivo.weev.backend.logging.ApplicationLoggingHelper;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class MailService {

    private final JavaMailSender mailSender;
    private final ApplicationLoggingHelper loggingHelper;

    @Async(value = "mailExecutor")
    public void sendHtmlMessage(MailMessage message) {
        MimeMessage mimeMessage = mailSender.createMimeMessage();
        try {
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true);
            getMapper(MimeMessageHelperMapper.class).map(message, helper);
            mailSender.send(mimeMessage);
        } catch (Exception exception) {
            log.error(loggingHelper.buildLoggingError(exception, null, true));
        }
    }
}
