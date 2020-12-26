package com.justmall.coupon;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class JustmallCouponApplication {

    public static void main(String[] args) {
        SpringApplication.run(JustmallCouponApplication.class, args);
    }

}
