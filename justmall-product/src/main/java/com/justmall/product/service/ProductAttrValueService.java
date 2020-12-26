package com.justmall.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.justmall.common.utils.PageUtils;
import com.justmall.product.entity.ProductAttrValueEntity;

import java.util.List;
import java.util.Map;

/**
 * spu属性值
 *
 * @author huwj
 * @email 470661893@qq.com
 * @date 2020-12-20 09:32:34
 */
public interface ProductAttrValueService extends IService<ProductAttrValueEntity> {

    PageUtils queryPage(Map<String, Object> params);
    void saveProductAttr(List<ProductAttrValueEntity> collect);

    /**
     * 查询规格属性
     */
    List<ProductAttrValueEntity> baseAttrListForSpu(Long spuId);

    /**
     * 更新属性的规格
     */
    void updateSpuAttr(Long spuId, List<ProductAttrValueEntity> entities);
}

