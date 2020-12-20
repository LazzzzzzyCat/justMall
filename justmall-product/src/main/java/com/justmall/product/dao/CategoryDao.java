package com.justmall.product.dao;

import com.justmall.product.entity.CategoryEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 商品三级分类
 * 
 * @author huwj
 * @email 470661893@qq.com
 * @date 2020-12-20 09:32:34
 */
@Mapper
public interface CategoryDao extends BaseMapper<CategoryEntity> {
	
}
