package com.example.spring_batch.common;

import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.JobParametersIncrementer;

import java.util.UUID;

@Slf4j
public class MyJobIdIncrementer implements JobParametersIncrementer {

    @Override
    public JobParameters getNext(JobParameters parameters) {
        UUID uuid = UUID.randomUUID();
        log.info("UUID => " + uuid.toString());
        return new JobParametersBuilder().addString("id", uuid.toString()).toJobParameters();
    }
}
