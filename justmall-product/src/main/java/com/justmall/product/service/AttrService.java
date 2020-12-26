package com.justmall.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.justmall.common.utils.PageUtils;
import com.justmall.product.entity.AttrEntity;
import com.justmall.product.entity.ProductAttrValueEntity;
import com.justmall.product.entity.vo.AttrGroupRelationVo;
import com.justmall.product.entity.vo.AttrResVo;
import com.justmall.product.entity.vo.AttrVo;

import java.util.List;
import java.util.Map;

/**
 * 商品属性
 *
 * @author huwj
 * @email 470661893@qq.com
 * @date 2020-12-20 09:32:34
 */
public interface AttrService extends IService<AttrEntity> {

    PageUtils queryPage(Map<String, Object> params);

    PageUtils queryByCid(Map<String, Object> params, Long catelogId, String attrType);

    void saveVo(AttrVo attr);

    AttrResVo getAttrInfo(Long attrId);

    void updateVo(AttrVo attr);

    List<ProductAttrValueEntity> getSpuSpecification(Long spuId);

    void updateSpecification(Long spuId, List<ProductAttrValueEntity> productAttrValueEntities);

    List<AttrEntity> getRelationAttr(Long attrgroupId);








    void deleteRelation(AttrGroupRelationVo[] vos);

    PageUtils getNoRelationAttr(Map<String, Object> params, Long attrgroupId);

    /**
     * 在指定的集合里面挑出可检索的属性
     */
    List<Long> selectSearchAttrIds(List<Long> attrIds);
}

