package com.example.spring_batch.mybatis.read.mappers;

import org.apache.ibatis.annotations.Mapper;

import java.util.HashMap;
@Mapper
public interface ReadTestMapper {
    Integer selectAllOfTestData();
    Integer selectPartitionOfTestData();
    HashMap<String, Object> selectMinMaxByTestId();
}
