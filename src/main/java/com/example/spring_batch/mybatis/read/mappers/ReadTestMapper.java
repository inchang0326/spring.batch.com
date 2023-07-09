package com.example.spring_batch.mybatis.read.mappers;

import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface ReadTestMapper {
    Integer selectAllOfTestData();
}
