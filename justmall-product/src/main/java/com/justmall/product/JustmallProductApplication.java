package com.justmall.product;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.justmall.product.dao")
public class JustmallProductApplication {

    public static void main(String[] args) {
        SpringApplication.run(JustmallProductApplication.class, args);
    }

}
