package com.example.spring_batch.jobs;

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

@Slf4j
@RequiredArgsConstructor
@Configuration
public class TestJob {

    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;
    private final SqlSessionFactory sqlSessionFactory;

    private final int CHUNK_SIZE = 2;

    @Bean(name = "TESTJOB01")
    public Job testJob() throws Exception {
        return jobBuilderFactory.get("TESTJOB01")
                .start(testStep(null, null))
                .build();
    }

    @Bean(name = "TESTJOB01_TESTSTEP01")
    @JobScope
    public Step testStep(@Value("#{jobParameters[date]}") String date
                        , @Value("#{jobParameters[ver]}") String ver) throws Exception {
        System.out.println("batchxs ver = " + ver + ", " + "date = " + date); // Batch Param debug

        return stepBuilderFactory.get("TESTJOB01_TESTSTEP01")
                .<Integer, Integer>chunk(CHUNK_SIZE)
                .reader(myBatisReader())
                .processor(processor())
                .writer(myBatisWriter())
                /*  배치 특성상 동일한 Param 통해 한 번 실행하여 성공하면, 다시 실행되지 않는 특성을 갖는다.
                    하지만 아래 chain 조건을 걸어주면, Param을 변경하지 않더라도 재실행이 가능해진다.
                */
                // .allowStartIfComplete(true)
                .build();
    }

    @Bean
    @StepScope
    public MyBatisPagingItemReader<Integer> myBatisReader() throws Exception {
        /*
         * Paging 처리 시 OrderBy는 필수
         */
        MyBatisPagingItemReader<Integer> reader = new MyBatisPagingItemReader<>();
        reader.setPageSize(CHUNK_SIZE);
        reader.setSqlSessionFactory(sqlSessionFactory);
        reader.setQueryId("selectAllOfTestData");
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
}
