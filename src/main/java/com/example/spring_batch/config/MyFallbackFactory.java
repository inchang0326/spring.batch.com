package com.example.spring_batch.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class MyFallbackFactory implements FallbackFactory<String> {

    @Override
    public String create(Throwable cause) {
        log.warn("Fallback Occured !!");
        return "fallback";
    }
}