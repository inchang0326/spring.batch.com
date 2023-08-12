package com.example.spring_batch.config;

import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.session.ExecutorType;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.listener.StepListenerSupport;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

@Slf4j
@Component
/*
    pros and cons
    pros : If a chunk fails, then all of chunks will be rolled back.
    cons : Performance Issue
           Mapper.insert() executes query immediately while addBatch() adds queries on memory and executeBatch() executes them at once.
 */
public class MyCustomWriter extends StepListenerSupport implements org.springframework.batch.item.ItemWriter<Integer> {

    @Autowired
    @Qualifier("writeSqlSessionFactory")
    private SqlSessionFactory writeSqlSessionFactory;
    private SqlSession sqlSession = null;
    private Connection connection = null;
    private boolean failed = false;
    private final String INSERT_TEST_DATA = "com.example.spring_batch.mybatis.write.mappers.WriteTestMapper.insertOneTestData";

    @Override
    public void beforeStep(StepExecution stepExecution) {
        sqlSession = writeSqlSessionFactory.openSession(ExecutorType.SIMPLE);
        connection = sqlSession.getConnection();
        try {
            connection.setAutoCommit(false);
        } catch(Exception e) {
        }
    }

    @Override
    public void write(List<? extends Integer> items) {
        try {
            for(Integer item : items) {
                sqlSession.insert(INSERT_TEST_DATA, item);}
        } catch (Exception e) {
            setFailState(true);
        }
    }

    @Override
    public void afterChunk(ChunkContext context){
        if(isFailed()) { // Writer 오류 발생 시, fail 처리
            context.getStepContext().getStepExecution().setExitStatus(ExitStatus.FAILED); // fail the step
            context.getStepContext().getStepExecution().setStatus(BatchStatus.FAILED); // fail the batch
        }
    }

    @Override
    public ExitStatus afterStep(StepExecution stepExecution) {
        try {
            if(stepExecution.getStatus() == BatchStatus.COMPLETED) {
                connection.commit();
            } else {
                connection.rollback();
            }
        } catch (SQLException sqle) {
            throw new RuntimeException(sqle);
        } finally {
            try {
                connection.close();
            } catch (SQLException sqle) {
                throw new RuntimeException(sqle);
            }
        }
        return stepExecution.getExitStatus();
    }

    private void setFailState(boolean failed){
        this.failed = failed;
    }

    private boolean isFailed() {
        return this.failed;
    }
}
