package com.pivo.weev.backend.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pivo.weev.backend.logging.ApplicationLoggingHelper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class LoggingBeans {

    @Bean
    public ApplicationLoggingHelper applicationLoggingHelper(ObjectMapper mapper) {
        return new ApplicationLoggingHelper(mapper);
    }
}
