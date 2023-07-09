package com.example.spring_batch.jobs;

import org.junit.After;
import org.junit.jupiter.api.Test;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.test.JobLauncherTestUtils;
import org.springframework.batch.test.context.SpringBatchTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBatchTest
@SpringBootTest(classes={TestJob.class})
class TestJobTest {
    @Autowired
    private JobLauncherTestUtils jobLauncherTestUtils;

    @After
    private void testAfter() {

    }

    @Test
    public void MultiThreadStepWithMyBatisPagingTest() throws Exception {

        // given
        JobParameters jobParameters = new JobParametersBuilder()
                .addString("ver", "2.0")
                .addString("date", "20230326")
                .addString("id", "hahahahahahahaha")
                .toJobParameters();
        // when
        JobExecution jobExecution = jobLauncherTestUtils.launchJob(jobParameters);

        // then
        assertThat(jobExecution.getStatus()).isEqualTo(BatchStatus.COMPLETED);
    }
}