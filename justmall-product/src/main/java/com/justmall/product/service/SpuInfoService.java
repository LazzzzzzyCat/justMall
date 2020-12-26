package com.justmall.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.justmall.common.utils.PageUtils;
import com.justmall.product.entity.SpuInfoEntity;
import com.justmall.product.entity.vo.SpuSaveVo;

import java.util.Map;

/**
 * spu信息
 *
 * @author huwj
 * @email 470661893@qq.com
 * @date 2020-12-20 09:32:34
 */
public interface SpuInfoService extends IService<SpuInfoEntity> {


    PageUtils queryPage(Map<String, Object> params);

    void saveSpuInfo(SpuSaveVo saveVo);

    void saveBatchSpuInfo(SpuInfoEntity spuInfoEntity);

    /**
     * SPU模糊查询
     */
    PageUtils queryByCondition(Map<String, Object> params);

    void up(Long spuId);

    /**
     * 返回一个SpuEntity
     */
    SpuInfoEntity getSpuInfoBySkuId(Long skuId);
}

