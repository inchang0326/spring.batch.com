package com.example.spring_batch.integration;

import com.example.spring_batch.config.DBConfiguration;
import com.example.spring_batch.config.MyBatisConfiguration;
import com.example.spring_batch.config.MyJobExecutionListener;
import com.example.spring_batch.config.MyTaskExecutor;
import com.example.spring_batch.jobs.TestJobWithMultiThreadSteps;
import org.junit.After;
import org.junit.jupiter.api.Test;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.test.JobLauncherTestUtils;
import org.springframework.batch.test.JobRepositoryTestUtils;
import org.springframework.batch.test.context.SpringBatchTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBatchTest
@EnableBatchProcessing
@SpringBootTest(classes={TestJobWithMultiThreadSteps.class
                        , DBConfiguration.class
                        , MyBatisConfiguration.class
                        , MyJobExecutionListener.class
                        , MyTaskExecutor.class})
class TestJobWithMultiThreadStepsTest {
    @Autowired
    private JobLauncherTestUtils jobLauncherTestUtils;

    @Autowired
    private JobRepositoryTestUtils jobRepositoryTestUtils;

    @After
    private void testAfter() {

    }

    @Test
    public void MultiThreadStepWithMyBatisPagingTest() throws Exception {

        // given
        JobParameters jobParameters = new JobParametersBuilder()
                .addString("ver", "5.0")
                .addString("date", "20260328")
                .addString("id", "1")
                .toJobParameters();
        // when
        // 신규 Job을 만들어 실행함
        // JobExecution jobExecution = jobLauncherTestUtils.launchJob(jobParameters);
        // 임시 Job을 만든 후 해당 Step 실행, Job과의 커플링 감소
        JobExecution jobExecution2 = jobLauncherTestUtils.launchStep("TESTJOB01_TESTSTEP01", jobParameters);

        // then
        assertThat(jobExecution2.getStatus()).isEqualTo(BatchStatus.COMPLETED);
    }
}