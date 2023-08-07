package com.example.spring_batch.jobs;

import com.example.spring_batch.common.MyJobIdIncrementer;
import com.example.spring_batch.common.MyJobParametersValidator;
import com.example.spring_batch.config.MyJobExecutionListener;
import com.example.spring_batch.config.MyTaskExecutor;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.batch.MyBatisBatchItemWriter;
import org.mybatis.spring.batch.MyBatisPagingItemReader;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.JobScope;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
public class TestJobWithSingleThreadSteps { // SingleThread Step

    @Autowired
    private JobBuilderFactory jobBuilderFactory;
    @Autowired
    private StepBuilderFactory stepBuilderFactory;

    @Autowired
    @Qualifier("readSqlSessionFactory")
    private SqlSessionFactory readSqlSessionFactory;
    @Autowired
    @Qualifier("writeSqlSessionFactory")
    private SqlSessionFactory writeSqlSessionFactory;

    @Autowired
    private MyJobExecutionListener myJobExecutionListener;
    @Autowired
    private MyTaskExecutor myTaskExecutor;

    private final int CHUNK_SIZE = 100;

    @Bean(name = "TESTJOB03")
    public Job testJob() throws Exception {
        return jobBuilderFactory.get("TESTJOB03")
                .start(testStep(null, null, null))
                .validator(new MyJobParametersValidator()) // JobParameters 검증용으로, Spring Batch 제공 DefaultjobParametrsValidator도 있다.
                .incrementer(new MyJobIdIncrementer()) // 새로운 JobParameters 넘겨주기 위한 incrementer. ※ 주의 : Program agrs가 incrementer 보다 우선순위여서 덮어쓴다.
                .listener(myJobExecutionListener.getMyJobExecutionListener(myTaskExecutor.getMyThreadPoolTaskExecutor()))
                .preventRestart() // 동일한 JobParameters로 실행하지 못하도록 막아주는 preventRestart()
                .build();
    }

    @Bean(name = "TESTJOB03_TESTSTEP01")
    @JobScope
    public Step testStep(@Value("#{jobParameters[ver]}") String ver
            , @Value("#{jobParameters[date]}") String date
            , @Value("#{jobParameters[id]}") String id) throws Exception {

        return stepBuilderFactory.get("TESTJOB03_TESTSTEP01")
                .<Integer, Integer>chunk(CHUNK_SIZE)
                .reader(myBatisPagingItemReader())
                .processor(itemProcessor())
                .writer(myBatisBatchItemWriter())
                /*  Spring Batch 특성상, complete된 JobExecution을 갖고 있는 JobInstance는 재실행 될 수 없다
                    하지만 allowStartIfComplete(true) chain 조건을 걸어주면, 재실행이 가능해진다.
                */
                //.allowStartIfComplete(true)
                .build();
    }

    @Bean("TESTJOB03_myBatisPagingItemReader")
    @StepScope
    public MyBatisPagingItemReader<Integer> myBatisPagingItemReader() throws Exception { // PagingItemReader thread-safe
        MyBatisPagingItemReader<Integer> reader = new MyBatisPagingItemReader<>(); // Paging 처리 시 쿼리 단 order by 필수
        reader.setSqlSessionFactory(readSqlSessionFactory);
        reader.setPageSize(CHUNK_SIZE);
        reader.setQueryId("selectAllOfTestData");
        reader.setSaveState(false);
        return reader;
    }

    @Bean("TESTJOB03_itemProcessor")
    @StepScope
    public ItemProcessor<Integer, Integer> itemProcessor() {
        return val -> {
            log.debug("val => " + val);
            return val;
        };
    }

    @Bean("TESTJOB03_myBatisBatchItemWriter")
    @StepScope
    public MyBatisBatchItemWriter<Integer> myBatisBatchItemWriter(){
        MyBatisBatchItemWriter<Integer> writer = new MyBatisBatchItemWriter<>();
        writer.setAssertUpdates(false);
        writer.setSqlSessionFactory(writeSqlSessionFactory);
        writer.setStatementId("insertOneTestData");
        return writer;
    }
}