package com.example.spring_batch.config;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;


@Configuration
public class MyTaskExecutor {

    @Qualifier
    @Bean
    public ThreadPoolTaskExecutor getMyThreadPoolTaskExecutor() {
        ThreadPoolTaskExecutor taskExecutor = new ThreadPoolTaskExecutor();
        taskExecutor.setCorePoolSize(4); // default thread-pool size
        taskExecutor.setMaxPoolSize(8); // the number of more needed threads when already default thread-pool size occupied
        taskExecutor.setThreadNamePrefix("async-thread"); // prefix of thread
        return taskExecutor;
    }
}
