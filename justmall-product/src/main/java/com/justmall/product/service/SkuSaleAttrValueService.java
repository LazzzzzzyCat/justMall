package com.justmall.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.justmall.common.utils.PageUtils;
import com.justmall.product.entity.SkuSaleAttrValueEntity;
import com.justmall.product.entity.vo.ItemSaleAttrVo;

import java.util.List;
import java.util.Map;

/**
 * sku销售属性&值
 *
 * @author huwj
 * @email 470661893@qq.com
 * @date 2020-12-20 09:32:34
 */
public interface SkuSaleAttrValueService extends IService<SkuSaleAttrValueEntity> {

    PageUtils queryPage(Map<String, Object> params);

    List<ItemSaleAttrVo> getSaleAttrsBuSpuId(Long spuId);

    List<String> getSkuSaleAttrValuesAsStringList(Long skuId);
}

