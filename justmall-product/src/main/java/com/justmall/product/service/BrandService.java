package com.justmall.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.justmall.common.utils.PageUtils;
import com.justmall.product.entity.BrandEntity;

import java.util.Map;

/**
 * 品牌
 *
 * @author huwj
 * @email 470661893@qq.com
 * @date 2020-12-20 09:32:34
 */
public interface BrandService extends IService<BrandEntity> {

    PageUtils queryPage(Map<String, Object> params);
}

