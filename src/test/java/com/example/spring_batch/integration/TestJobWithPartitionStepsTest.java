package com.example.spring_batch.integration;

import com.example.spring_batch.config.*;
import com.example.spring_batch.jobs.TestJobWithPartitionSteps;
import org.junit.jupiter.api.Test;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.test.JobLauncherTestUtils;
import org.springframework.batch.test.context.SpringBatchTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;

@MapperScan("com.example.spring_batch.mybatis.read.mappers")
@SpringBatchTest
@EnableBatchProcessing
@SpringBootTest(classes={TestJobWithPartitionSteps.class
                        , DBConfiguration.class
                        , MyBatisConfiguration.class
                        , MyJobExecutionListener.class
                        , MyTaskExecutor.class
                        , CustomPartitioner.class})
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class TestJobWithPartitionStepsTest {

    @Autowired
    private JobLauncherTestUtils jobLauncherTestUtils;

    @Test
    public void PartitionStepWithMyBatisPagingTest() throws Exception {

        // given
        JobParameters jobParameters = new JobParametersBuilder()
                .addString("ver", "4.0")
                .addString("date", "20200103")
                .addString("id", "2")
                .toJobParameters();
        // when
        // 신규 Job을 만들어 실행함
//        JobExecution jobExecution = jobLauncherTestUtils.launchJob(jobParameters);
        // 임시 Job을 만든 후 해당 Step 실행, Job과의 커플링 감소
        JobExecution jobExecution2 = jobLauncherTestUtils.launchStep("TESTJOB02_MASTER_TESTSTEP01", jobParameters);

        // then
        assertThat(jobExecution2.getStatus()).isEqualTo(BatchStatus.COMPLETED);
    }
}