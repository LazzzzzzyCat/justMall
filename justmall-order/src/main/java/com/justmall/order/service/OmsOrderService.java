package com.justmall.order.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.justmall.common.utils.PageUtils;
import com.justmall.order.entity.OmsOrderEntity;

import java.util.Map;

/**
 * 订单
 *
 * @author huwj
 * @email 470661893@qq.com
 * @date 2020-12-20 10:55:18
 */
public interface OmsOrderService extends IService<OmsOrderEntity> {

    PageUtils queryPage(Map<String, Object> params);
}

