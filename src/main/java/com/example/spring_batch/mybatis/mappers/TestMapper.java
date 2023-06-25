package com.example.spring_batch.mybatis.mappers;

import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface TestMapper {
    Integer selectAllOfTestData();
    void insertOneTestData();
}
