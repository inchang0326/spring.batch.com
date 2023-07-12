package com.example.spring_batch.apis;

import com.example.spring_batch.config.FeignClientConfiguration;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

@FeignClient(name="testFeignClient", url="https://www.naver.com/", configuration = FeignClientConfiguration.class)
public interface TestFeignClient {

    @GetMapping("/")
    public String helloWorld();
}