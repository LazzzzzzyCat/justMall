package com.justmall.order.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.justmall.common.utils.PageUtils;
import com.justmall.order.entity.UndoLogEntity;

import java.util.Map;

/**
 * 
 *
 * @author huwj
 * @email 470661893@qq.com
 * @date 2020-12-20 10:55:18
 */
public interface UndoLogService extends IService<UndoLogEntity> {

    PageUtils queryPage(Map<String, Object> params);
}

