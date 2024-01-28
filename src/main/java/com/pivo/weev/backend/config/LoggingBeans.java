package com.pivo.weev.backend.config;

import com.google.gson.Gson;
import com.pivo.weev.backend.logging.ApplicationLoggingHelper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class LoggingBeans {

    @Bean
    public ApplicationLoggingHelper applicationLoggingHelper(Gson gson) {
        return new ApplicationLoggingHelper(gson);
    }
}
