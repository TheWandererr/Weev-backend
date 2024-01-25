package com.pivo.weev.backend.config.messaging;

import static java.nio.charset.StandardCharsets.UTF_8;

import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;

@Configuration
public class MessageSources {

    @Bean
    public MessageSource emailMessages() {
        ReloadableResourceBundleMessageSource messageSource = new ReloadableResourceBundleMessageSource();
        messageSource.setBasename("classpath:i18n/email-messages");
        messageSource.setDefaultEncoding(UTF_8.name());
        messageSource.setCacheSeconds(10);
        return messageSource;
    }
}
