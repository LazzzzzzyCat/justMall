package com.justmall;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * @author huwj
 * @date 2020/12/22 21:17
 */
@EnableDiscoveryClient
@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class})
public class JustmallThridPartyApplication {

    public static void main(String[] args) {
        SpringApplication.run(JustmallThridPartyApplication.class, args);
    }
}
