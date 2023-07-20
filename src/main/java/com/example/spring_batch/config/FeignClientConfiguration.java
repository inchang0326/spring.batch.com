package com.example.spring_batch.config;

import feign.Client;
import feign.Logger;
import feign.httpclient.ApacheHttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.conn.HttpClientConnectionManager;
import org.apache.http.impl.client.CloseableHttpClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.commons.httpclient.ApacheHttpClientFactory;
import org.springframework.cloud.openfeign.support.FeignHttpClientProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.TimeUnit;

@Configuration // Feign Config class에 @Configuration 설정을 하면 각 client에서 config class 설정을 하지 않더라도 모든 client에 디폴트로 적용된다.
public class FeignClientConfiguration {

    @Value("${feign.httpclient.socketTimeout}")
    private int socketTimeout;

    @Value("${rest.client.conn.max.idle.time}")
    private long maxIdleTimeout;

    @Bean
    Logger.Level feignLoggerLevel() {
        return Logger.Level.FULL;
    }

    @Bean
    public Client feignClient(ApacheHttpClientFactory httpClientFactory,
                              HttpClientConnectionManager httpClientConnectionManager,
                              FeignHttpClientProperties httpClientProperties) {
        RequestConfig defaultRequestConfig = RequestConfig
                .custom()
                .setConnectTimeout(httpClientProperties.getConnectionTimeout()) // connection timeout
                .setSocketTimeout(socketTimeout) // read timeout
                .setRedirectsEnabled(httpClientProperties.isFollowRedirects())
                .build();

        CloseableHttpClient httpClient = httpClientFactory
                .createBuilder()
                .setConnectionManager(httpClientConnectionManager)
                .setMaxConnTotal(httpClientProperties.getMaxConnections()) // pool of connection limit and connection reuse
                .setMaxConnPerRoute(httpClientProperties.getMaxConnectionsPerRoute())
                .setDefaultRequestConfig(defaultRequestConfig)
                .evictIdleConnections(maxIdleTimeout, TimeUnit.MILLISECONDS) // keep alive duration
                .evictExpiredConnections()
                .build();

        return new ApacheHttpClient(httpClient);
    }
}