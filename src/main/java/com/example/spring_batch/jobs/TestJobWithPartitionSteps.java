package com.example.spring_batch.jobs;

import com.example.spring_batch.common.MyJobIdIncrementer;
import com.example.spring_batch.common.MyJobParametersValidator;
import com.example.spring_batch.config.CustomPartitioner;
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

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Configuration
public class TestJobWithPartitionSteps { // Partition Steps
    @Autowired
    private JobBuilderFactory jobBuilderFactory;
    @Autowired
    private StepBuilderFactory stepBuilderFactory;
    @Autowired
    private CustomPartitioner customPartitioner;
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

    private final int CHUNK_SIZE = myTaskExecutor.getPoolSize();

    @Bean(name = "TESTJOB02")
    public Job testJob() throws Exception {
        return jobBuilderFactory.get("TESTJOB02")
                .start(masterTestStep(null, null, null))
                .validator(new MyJobParametersValidator()) // JobParameters 검증용으로, Spring Batch 제공 DefaultjobParametrsValidator도 있다.
                .incrementer(new MyJobIdIncrementer()) // 새로운 JobParameters 넘겨주기 위한 incrementer. ※ 주의 : Program agrs가 incrementer 보다 우선순위여서 덮어쓴다.
                .listener(myJobExecutionListener.getMyJobExecutionListener(myTaskExecutor.getMyThreadPoolTaskExecutor()))
                .preventRestart() // 동일한 JobParameters로 실행하지 못하도록 막아주는 preventRestart()
                .build();
    }

    @Bean(name = "TESTJOB02_MASTER_TESTSTEP01")
    @JobScope
    public Step masterTestStep(@Value("#{jobParameters[ver]}") String ver
            , @Value("#{jobParameters[date]}") String date
            , @Value("#{jobParameters[id]}") String id) throws Exception {

        return stepBuilderFactory.get("TESTJOB02_MASTER_TESTSTEP01")
                .partitioner("TESTJOB02_SLAVE_TESTSTEP01", customPartitioner)
                .step(slaveTestStep())
                .gridSize(myTaskExecutor.getPoolSize())
                .taskExecutor(myTaskExecutor.getMyThreadPoolTaskExecutor())
                .build();
    }

    @Bean(name = "TESTJOB02_SLAVE_TESTSTEP01")
    public Step slaveTestStep() throws Exception {
        return stepBuilderFactory.get("TESTJOB02_SLAVE_TESTSTEP01")
                .<Integer, Integer>chunk(CHUNK_SIZE)
                .reader(myBatisPagingItemReader(null, null))
                .processor(itemProcessor(null, null))
                .writer(myBatisBatchItemWriter(null, null))
                .build();
    }

    @Bean("TESTJOB02_myBatisPagingItemReader")
    @StepScope
    public MyBatisPagingItemReader<Integer> myBatisPagingItemReader(@Value("#{stepExecutionContext[minTestId]}") Long minTestId
                                                                    , @Value("#{stepExecutionContext[maxTestId]}") Long maxTestId) throws Exception { // PagingItemReader thread-safe

        Map<String, Object> params = new HashMap<>();
        params.put("minTestId", minTestId);
        params.put("maxTestId", maxTestId);

        MyBatisPagingItemReader<Integer> reader = new MyBatisPagingItemReader<>(); // Paging 처리 시 쿼리 단 order by 필수
        reader.setSqlSessionFactory(readSqlSessionFactory);
        reader.setPageSize(CHUNK_SIZE); // Partition Steps 기법을 사용한다 하더라도, Chunk Oriented Tasklit으로써 Pagination은 해줘야 함.
        reader.setParameterValues(params);
        reader.setQueryId("selectPartitionOfTestData");
        reader.setSaveState(false); // 실패한 지점을 기록하여 실패 지점부터 재실행하게 해주는 setSaveState(). ※ 주의 : MutliThread 기반으로 처리할 때는 상태관리가 안되기 때문에 false 처리 해두어야 함
        return reader;
    }

    @Bean("TESTJOB02_itemProcessor")
    @StepScope
    public ItemProcessor<Integer, Integer> itemProcessor(@Value("#{stepExecutionContext[minTestId]}") Long minTestId
                                                        , @Value("#{stepExecutionContext[maxTestId]}") Long maxTestId) {
        return val -> {
            log.debug("minTestId : " + minTestId + " maxTestId : " + maxTestId + " val : " + val);
            return val;
        };
    }

    @Bean("TESTJOB02_myBatisBatchItemWriter")
    @StepScope
    public MyBatisBatchItemWriter<Integer> myBatisBatchItemWriter(@Value("#{stepExecutionContext[minTestId]}") Long minTestId
                                                                , @Value("#{stepExecutionContext[maxTestId]}") Long maxTestId){
        MyBatisBatchItemWriter<Integer> writer = new MyBatisBatchItemWriter<>();
        writer.setAssertUpdates(false);
        writer.setSqlSessionFactory(writeSqlSessionFactory);
        writer.setStatementId("insertOneTestData");
        return writer;
    }
}
