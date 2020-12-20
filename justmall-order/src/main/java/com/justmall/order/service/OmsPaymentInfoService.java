package com.justmall.order.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.justmall.common.utils.PageUtils;
import com.justmall.order.entity.OmsPaymentInfoEntity;

import java.util.Map;

/**
 * 支付信息表
 *
 * @author huwj
 * @email 470661893@qq.com
 * @date 2020-12-20 10:55:18
 */
public interface OmsPaymentInfoService extends IService<OmsPaymentInfoEntity> {

    PageUtils queryPage(Map<String, Object> params);
}

