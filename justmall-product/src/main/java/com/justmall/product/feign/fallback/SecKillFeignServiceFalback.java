package com.justmall.product.feign.fallback;

import com.justmall.common.exception.BizCodeEnum;
import com.justmall.common.utils.R;
import com.justmall.product.feign.SeckillFeignService;
import org.springframework.stereotype.Component;

/**
 * <p>Title: SecKillFeignServiceFalback</p>
 * Description：
 * date：2020/7/10 16:03
 */
@Component
public class SecKillFeignServiceFalback implements SeckillFeignService {

	@Override
	public R getSkuSeckillInfo(Long skuId) {
		System.out.println("触发熔断");
		return R.error(BizCodeEnum.TO_MANY_REQUEST.getCode(), BizCodeEnum.TO_MANY_REQUEST.getMsg());
	}
}
