package com.example.spring_batch.config;

import feign.Client;
import feign.Logger;
import feign.httpclient.ApacheHttpClient;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.conn.routing.HttpRoute;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.pool.ConnPoolControl;
import org.springframework.cloud.commons.httpclient.ApacheHttpClientFactory;
import org.springframework.cloud.openfeign.support.FeignHttpClientProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.TimeUnit;

@Slf4j
@Configuration // Feign Config class에 @Configuration 설정을 하면 각 client에서 config class 설정을 하지 않더라도 모든 client에 디폴트로 적용된다.
public class FeignClientConfiguration {

    @Bean
    public Logger.Level feignLoggerLevel() {
        return Logger.Level.FULL;
    }

    @Bean
    public Client feignClient(ApacheHttpClientFactory httpClientFactory,
                              FeignHttpClientProperties httpClientProperties) {
        PoolingHttpClientConnectionManager cm = new PoolingHttpClientConnectionManager(); // idle connection timeout(time to live) can be passed by constructor's params
        cm.setMaxTotal(200); // the number of connections
        cm.setDefaultMaxPerRoute(200); // the number of routes per connections
        CloseableHttpClient httpClient = httpClientFactory
                .createBuilder()
                .setConnectionManager(cm) // HttpClient's attributes are overwritten by Connection Mannager
                .evictIdleConnections(60, TimeUnit.SECONDS) // idle connection timeout(time to live) can be set by HttpClient's attribute
                .evictExpiredConnections() // get rid of idle connection in effect
                .build();

        // Idle Connection Monitoring
//        Thread th = new IdleConnectionMonitorThread(cm);
//        th.start();

        return new ApacheHttpClient(httpClient);
    }

    static class IdleConnectionMonitorThread extends Thread {
        private volatile boolean shutdown;
        private PoolingHttpClientConnectionManager cm;

        public IdleConnectionMonitorThread(PoolingHttpClientConnectionManager cm) {
            super();
            this.setName("TCP-Idle-Connection-Monitoring-Thread");
            this.cm = cm;
        }

        @Override
        public void run() {
            ConnPoolControl<HttpRoute> cpc = cm;
            try {
                while (!shutdown) {
                    sleep(1000);
                    log.debug("Connection Pool Status => " + cpc.getTotalStats());
                }
            } catch (InterruptedException ex) {
            }
        }

        public void shutdown() {
            shutdown = true;
        }
    }
}