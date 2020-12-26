package com.justmall.product.config;

import com.baomidou.mybatisplus.extension.plugins.PaginationInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * @author huwj
 * @date 2020/12/25 21:52
 */
@Configuration
@EnableTransactionManagement
public class MybatisConfig {
    /**
     * mp 分页插件
     *
     * @return
     */
    @Bean
    public PaginationInnerInterceptor paginationInterceptor() {
        PaginationInnerInterceptor paginationInterceptor = new PaginationInnerInterceptor();
        // 设置请求的页面，true为回到首页，false为继续请求
        paginationInterceptor.setOverflow(true);
        // 每页最大为1000条 （默认500）
        paginationInterceptor.setMaxLimit(1000L);
        return paginationInterceptor;
    }
}
