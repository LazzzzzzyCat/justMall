package com.justmall.ware.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * @author huwj
 * @date 2020/12/25 22:59
 */
@FeignClient(value = "gulimall-product")
public interface ProductFeignService {

    @GetMapping("/product/skuinfo/getSkuName")
    String getSkuName(@RequestParam Long skuId);
}
