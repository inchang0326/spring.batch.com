package com.example.spring_batch.jobs;

import com.example.spring_batch.mybatis.mappers.TestMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Slf4j
@RequiredArgsConstructor
@Configuration
public class TestJobConfiguration {

    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;

    private final TestMapper testMapper;

    @Bean(name = "TESTJOB01")
    public Job testJob() {
        return jobBuilderFactory.get("TESTJOB01")
                .start(testStep())
                .build();
    }

    @Bean(name = "TESTJOB01_TESTSTEP02")
    public Step testStep() {
        return stepBuilderFactory.get("TESTJOB01_TESTSTEP02")
                .tasklet((contribution, chunkContext) -> {
                    log.info("step >>>>");
                    int a = testMapper.selectTotalCnt();
                    System.out.println(a);
                    return RepeatStatus.FINISHED;
                })
                .allowStartIfComplete(true)
                .build();
    }
}
