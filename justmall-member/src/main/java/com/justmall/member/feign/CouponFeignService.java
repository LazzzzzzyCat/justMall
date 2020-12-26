package com.justmall.member.feign;

import com.justmall.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @author huwj
 * @date 2020/12/20 17:19
 */
@FeignClient("justmall-coupon")
public interface CouponFeignService {

    @RequestMapping("counpon/coupon/member/list")
    R memberCoupons();
}
