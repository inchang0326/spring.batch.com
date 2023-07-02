package com.example.spring_batch.jobs;

import com.example.spring_batch.common.MyJobIdIncrementer;
import com.example.spring_batch.common.MyJobParametersValidator;
import com.example.spring_batch.config.MyTaskExecutor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.batch.MyBatisBatchItemWriter;
import org.mybatis.spring.batch.MyBatisPagingItemReader;
import org.springframework.batch.core.*;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.JobScope;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class TestJob {

    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;
    private final SqlSessionFactory sqlSessionFactory;
    private final MyTaskExecutor myTaskExecutor;

    private final int CHUNK_SIZE = 2;

    @Bean(name = "TESTJOB01")
    public Job testJob() throws Exception {
        return jobBuilderFactory.get("TESTJOB01")
                .start(testStep(null, null, null))
                .validator(new MyJobParametersValidator()) // JobParameters 검증용으로, Spring Batch 제공 DefaultjobParametrsValidator도 있다.
                .incrementer(new MyJobIdIncrementer()) // 새로운 JobParameters 넘겨주기 위한 incrementer. ※ 주의 : Program agrs가 incrementer 보다 우선순위여서 덮어쓴다.
                .listener(myJobExecutionListener())
                .preventRestart() // 동일한 JobParameters로 실행하지 못하도록 막아주는 preventRestart()
                .build();
    }

    @Bean(name = "TESTJOB01_TESTSTEP01")
    @JobScope
    public Step testStep(@Value("#{jobParameters[ver]}") String ver
                        , @Value("#{jobParameters[date]}") String date
                        , @Value("#{jobParameters[id]}") String id) throws Exception {
        return stepBuilderFactory.get("TESTJOB01_TESTSTEP01")
                .<Integer, Integer>chunk(CHUNK_SIZE)
                .reader(myBatisReader())
                .processor(processor())
                .writer(myBatisWriter())
                /*  Spring Batch 특성상, complete된 JobExecution을 갖고 있는 JobInstance는 재실행 될 수 없다
                    하지만 allowStartIfComplete(true) chain 조건을 걸어주면, 재실행이 가능해진다.
                */
                //.allowStartIfComplete(true)
                .taskExecutor(myTaskExecutor.get())
                .build();
    }

    @Bean
    @StepScope
    public MyBatisPagingItemReader<Integer> myBatisReader() throws Exception { // PagingItemReader thread-safe
        MyBatisPagingItemReader<Integer> reader = new MyBatisPagingItemReader<>(); // Paging 처리 시 쿼리 단 order by 필수
        reader.setPageSize(CHUNK_SIZE);
        reader.setSqlSessionFactory(sqlSessionFactory);
        reader.setQueryId("selectAllOfTestData");
        reader.setSaveState(false); // 실패한 지점을 기록하여 실패 지점부터 재실행하게 해주는 setSaveState(). ※ 주의 : Async 처리할 때는 false 처리 해두어야 함
        return reader;
    }

    @Bean
    @StepScope
    public ItemProcessor<Integer, Integer> processor() {
        return new ItemProcessor<Integer, Integer>() {
            @Override
            public Integer process(Integer val) throws Exception {
                System.out.println("val => " + val);
                return val;
            }
        };
    }

    @Bean
    @StepScope
    public MyBatisBatchItemWriter<Integer> myBatisWriter(){
        MyBatisBatchItemWriter<Integer> writer = new MyBatisBatchItemWriter<>();
        writer.setAssertUpdates(false);
        writer.setSqlSessionFactory(sqlSessionFactory);
        writer.setStatementId("insertOneTestData");
        return writer;
    }

    @Bean
    public JobExecutionListener myJobExecutionListener() {
        return new JobExecutionListener() {
            @Override
            public void beforeJob(JobExecution jobExecution) {
                log.debug("[JobExecutionListener#beforeJob] jobExecution is " + jobExecution.getStatus());
            }

            @Override
            public void afterJob(JobExecution jobExecution) {
                ThreadPoolTaskExecutor threadPoolTaskExecutor = myTaskExecutor.get();

                if(jobExecution.getStatus() == BatchStatus.COMPLETED) {
                    // todo
                } else if(jobExecution.getStatus() == BatchStatus.FAILED) {
                    // todo
                }

                threadPoolTaskExecutor.shutdown();
                log.debug("[JobExecutionListener#afterJob] jobExecution is " + jobExecution.getStatus());
            }
        };
    }
}
