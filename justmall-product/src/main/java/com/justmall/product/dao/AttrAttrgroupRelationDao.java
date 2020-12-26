package com.justmall.product.dao;

import com.justmall.product.entity.AttrAttrgroupRelationEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 属性&属性分组关联
 * 
 * @author huwj
 * @email 470661893@qq.com
 * @date 2020-12-20 09:32:34
 */
@Mapper
public interface AttrAttrgroupRelationDao extends BaseMapper<AttrAttrgroupRelationEntity> {
    void deleteBathRelations(@Param("entities") List<AttrAttrgroupRelationEntity> entityList);
}
