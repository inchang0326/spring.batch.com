package com.example.spring_batch.apis.feignclient;

import com.example.spring_batch.config.MyFallbackFactory;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

@FeignClient(name="testFeignClient"
            , url="localhost:8081"
            , fallbackFactory = MyFallbackFactory.class)
public interface TestFeignClient {

    @GetMapping("/fruit/get") // 해당 Spring Annotation을 Feign과 함께 사용할 수 있는 이유는, Feign의 디폴트 Contract가 SpringMvcContract이기 때문이다.
    public String helloWorld();
}