package com.justmall.product.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author huwj
 * @date 2020/12/26 10:45
 */
@Configuration
public class MyThreadConfig {
    @Bean
    public ThreadPoolExecutor threadPoolExecutor(ThreadPoolConfigProperties threadPoolConfigProperties){

        return new ThreadPoolExecutor(threadPoolConfigProperties.getCoreSize(), threadPoolConfigProperties.getMaxSize(), threadPoolConfigProperties.getKeepAliveTime() , TimeUnit.SECONDS, new LinkedBlockingDeque<>(10000), Executors.defaultThreadFactory(),new ThreadPoolExecutor.AbortPolicy());
    }
}
