package com.pivo.weev.backend.config.rabbit;

import com.pivo.weev.backend.config.rabbit.properties.RabbitProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitConfig {

    @Bean
    @ConfigurationProperties(prefix = "spring.rabbitmq")
    public RabbitProperties rabbitProperties() {
        return new RabbitProperties();
    }
}
