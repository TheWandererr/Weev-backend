package com.pivo.weev.backend.config.springdoc;

import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SpringDocConfig {

    @Bean
    public GroupedOpenApi controllerApi() {
        return GroupedOpenApi.builder()
                             .group("controller-api")
                             .packagesToScan("com.pivo.weev.backend")
                             .build();
    }
}
