package com.example.spring_batch.apis.controller;

import com.example.spring_batch.apis.feignclient.TestFeignClient;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController("test")
@RequiredArgsConstructor
public class TestController {
    private final TestFeignClient testFeignClient;

    @GetMapping("/greet")
    public String get() {
        return testFeignClient.helloWorld();
    }
}
