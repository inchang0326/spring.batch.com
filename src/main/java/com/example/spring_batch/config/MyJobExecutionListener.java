package com.example.spring_batch.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobExecutionListener;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@Slf4j
@Configuration
public class MyJobExecutionListener {

    private long startTime = 0;
    private long endTime = 0;

    @Bean
    public JobExecutionListener getMyJobExecutionListener(ThreadPoolTaskExecutor threadPoolTaskExecutor) {
        return new JobExecutionListener() {
            @Override
            public void beforeJob(JobExecution jobExecution) {
                startTime = System.currentTimeMillis();
                log.debug("[JobExecutionListener#beforeJob] jobExecution is " + jobExecution.getStatus());
            }

            @Override
            public void afterJob(JobExecution jobExecution) {
                if(jobExecution.getStatus() == BatchStatus.COMPLETED) {
                    // todo
                } else if(jobExecution.getStatus() == BatchStatus.FAILED) {
                    // todo
                }

                log.debug("[JobExecutionListener#afterJob] jobExecution is " + jobExecution.getStatus());
                threadPoolTaskExecutor.shutdown();

                endTime = System.currentTimeMillis();
                log.debug("Total Process Time is " + ((endTime - startTime) / 1000) + "s");
            }
        };
    }
}
