package com.pivo.weev.backend.config.async;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.TaskDecorator;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@Configuration
@EnableAsync
public class AsyncConfig {

    @Bean
    public ThreadPoolTaskExecutor mailExecutor(ExecutorProperties mailExecutorProperties, TaskDecorator taskDecorator) {
        return initExecutor(mailExecutorProperties, taskDecorator);
    }

    @Bean
    public ThreadPoolTaskExecutor commonExecutor(ExecutorProperties commonExecutorProperties, TaskDecorator taskDecorator) {
        return initExecutor(commonExecutorProperties, taskDecorator);
    }

    @Bean
    @ConfigurationProperties(prefix = "application.mail.async.executor")
    public ExecutorProperties mailExecutorProperties() {
        return new ExecutorProperties();
    }

    @Bean
    @ConfigurationProperties(prefix = "application.common.async.executor")
    public ExecutorProperties commonExecutorProperties() {
        return new ExecutorProperties();
    }

    private ThreadPoolTaskExecutor initExecutor(ExecutorProperties executorProperties, TaskDecorator taskDecorator) {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(executorProperties.getCorePoolSize());
        executor.setMaxPoolSize(executorProperties.getMaxPoolSize());
        executor.setQueueCapacity(executorProperties.getQueueCapacity());
        executor.setThreadNamePrefix(executorProperties.getPrefix());
        executor.setTaskDecorator(taskDecorator);
        executor.initialize();
        return executor;
    }
}
