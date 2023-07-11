package com.example.spring_batch.config;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;


@Configuration
public class MyTaskExecutor {

    private final int poolSize = 4;

    @Bean
    public ThreadPoolTaskExecutor getMyThreadPoolTaskExecutor() {
        ThreadPoolTaskExecutor taskExecutor = new ThreadPoolTaskExecutor();
        taskExecutor.setCorePoolSize(poolSize); // default thread-pool size
        taskExecutor.setMaxPoolSize(poolSize); // the number of more needed threads when already default thread-pool size occupied
        taskExecutor.setThreadNamePrefix("async-thread"); // prefix of thread
        taskExecutor.setWaitForTasksToCompleteOnShutdown(Boolean.TRUE);
        return taskExecutor;
    }

    public int getPoolSize() {
        return this.poolSize;
    }
}
