package com.pivo.weev.backend.domain.mapping.domain;

import com.pivo.weev.backend.domain.model.messaging.MailMessage;
import jakarta.mail.MessagingException;
import org.mapstruct.Mapper;
import org.springframework.mail.javamail.MimeMessageHelper;

@Mapper
public interface MimeMessageHelperMapper {

    default void map(MailMessage source, MimeMessageHelper destination) throws MessagingException {
        destination.setTo(source.getRecipient());
        destination.setSubject(source.getSubject());
        destination.setText(source.getContent(), true);
    }
}
