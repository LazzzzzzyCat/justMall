package com.justmall.ware.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.justmall.common.utils.PageUtils;
import com.justmall.ware.entity.WmsWareOrderTaskDetailEntity;

import java.util.Map;

/**
 * 库存工作单
 *
 * @author huwj
 * @email 470661893@qq.com
 * @date 2020-12-20 10:56:39
 */
public interface WmsWareOrderTaskDetailService extends IService<WmsWareOrderTaskDetailEntity> {

    PageUtils queryPage(Map<String, Object> params);
}

