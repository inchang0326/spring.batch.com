package com.example.spring_batch.config;

import com.example.spring_batch.mybatis.read.mappers.ReadTestMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.partition.support.Partitioner;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class CustomPartitioner implements Partitioner {

    private final ReadTestMapper readTestMapper;
    @Override
    public Map<String, ExecutionContext> partition(int gridSize) {
        HashMap<String, Object> resMap = readTestMapper.selectMinMaxByTestId();
        HashMap<String, ExecutionContext> partition = new HashMap<>();
        int minTestId = (Integer) resMap.get("min_test_id");
        int maxTestId = (Integer) resMap.get("max_test_id");
        int eachValue = ((maxTestId - minTestId) / gridSize) + 1 ;

        log.info("min : " + minTestId + " max : " + maxTestId);

        int start = minTestId;
        int end = eachValue;

        for(int i=0; i<gridSize; i++) {
            ExecutionContext executionContext = new ExecutionContext();
            executionContext.put("minTestId", start);
            executionContext.put("maxTestId", end);

            partition.put(String.valueOf(i), executionContext);

            start = end + 1;
            end = end + eachValue;
        }
        return partition;
    }
}