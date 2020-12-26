package com.justmall.product.dao;

import com.justmall.product.entity.SpuInfoEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * spu信息
 * 
 * @author huwj
 * @email 470661893@qq.com
 * @date 2020-12-20 09:32:34
 */
@Mapper
public interface SpuInfoDao extends BaseMapper<SpuInfoEntity> {
    /**
     * 修改上架成功的商品的状态
     */
    void updateSpuStatus(@Param("spuId") Long spuId, @Param("code") int code);
}
