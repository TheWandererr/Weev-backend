package com.pivo.weev.backend.config.lifecycle;

import java.util.HashMap;
import java.util.Map;
import org.springframework.beans.factory.config.CustomScopeConfigurer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.SimpleThreadScope;

@Configuration
public class BeanScopeConfigurer {

    @Bean
    public static CustomScopeConfigurer customScopeConfigurer() {
        CustomScopeConfigurer newConfigurer = new CustomScopeConfigurer();
        Map<String, Object> newScopes = new HashMap<>();
        newScopes.put("thread", new SimpleThreadScope());
        newConfigurer.setScopes(newScopes);
        return newConfigurer;
    }
}
