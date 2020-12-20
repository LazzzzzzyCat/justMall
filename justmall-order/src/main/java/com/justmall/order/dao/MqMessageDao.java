package com.justmall.order.dao;

import com.justmall.order.entity.MqMessageEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 
 * 
 * @author huwj
 * @email 470661893@qq.com
 * @date 2020-12-20 10:55:18
 */
@Mapper
public interface MqMessageDao extends BaseMapper<MqMessageEntity> {
	
}
