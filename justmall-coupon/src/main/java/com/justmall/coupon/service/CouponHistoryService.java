package com.justmall.coupon.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.justmall.common.utils.PageUtils;
import com.justmall.coupon.entity.CouponHistoryEntity;

import java.util.Map;

/**
 * 优惠券领取历史记录
 *
 * @author huwj
 * @email 470661893@qq.com
 * @date 2020-12-20 10:50:01
 */
public interface CouponHistoryService extends IService<CouponHistoryEntity> {

    PageUtils queryPage(Map<String, Object> params);
}

