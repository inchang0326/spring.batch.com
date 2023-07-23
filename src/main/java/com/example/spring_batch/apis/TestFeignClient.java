package com.example.spring_batch.apis;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

@FeignClient(name="testFeignClient", url="https://d548c4d8-8aa5-4c35-b65b-011a27e488b4.mock.pstmn.io")
public interface TestFeignClient {

    @GetMapping("/helloWorld") // 해당 Spring Annotation을 Feign과 함께 사용할 수 있는 이유는, Feign의 디폴트 Contract가 SpringMvcContract이기 때문이다.
    public String helloWorld();
}