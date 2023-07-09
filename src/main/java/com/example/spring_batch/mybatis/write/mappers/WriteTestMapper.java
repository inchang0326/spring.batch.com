package com.example.spring_batch.mybatis.write.mappers;

import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface WriteTestMapper {
    void insertOneTestData();
}
