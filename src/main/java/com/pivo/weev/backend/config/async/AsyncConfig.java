package com.pivo.weev.backend.config.async;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@Configuration
@EnableAsync
public class AsyncConfig {

    @Bean
    public ThreadPoolTaskExecutor mailExecutor(ExecutorProperties mailExecutorProperties) {
        return initExecutor(mailExecutorProperties);
    }

    @Bean
    @ConfigurationProperties(prefix = "application.mail.async.executor")
    public ExecutorProperties mailExecutorProperties() {
        return new ExecutorProperties();
    }

    private ThreadPoolTaskExecutor initExecutor(ExecutorProperties executorProperties) {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(executorProperties.getCorePoolSize());
        executor.setMaxPoolSize(executorProperties.getMaxPoolSize());
        executor.setQueueCapacity(executorProperties.getQueueCapacity());
        executor.setThreadNamePrefix(executorProperties.getPrefix());
        executor.initialize();
        return executor;
    }
}
