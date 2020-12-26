package com.justmall.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.justmall.common.utils.PageUtils;
import com.justmall.product.entity.AttrEntity;
import com.justmall.product.entity.AttrGroupEntity;
import com.justmall.product.entity.vo.AttrGroupAndAttrVo;
import com.justmall.product.entity.vo.AttrGroupRelationVo;
import com.justmall.product.entity.vo.SpuItemAttrGroup;

import java.util.List;
import java.util.Map;

/**
 * 属性分组
 *
 * @author huwj
 * @email 470661893@qq.com
 * @date 2020-12-20 09:32:34
 */
public interface AttrGroupService extends IService<AttrGroupEntity> {
    PageUtils queryPage(Map<String, Object> params);

    PageUtils queryPageByCid(Map<String, Object> params, Long catelogId);

    List<AttrEntity> getAttrByAttrGroupId(Long attrgroupId);

    void deleteRelation(AttrGroupRelationVo[] attrGroupRelationVo);

    PageUtils getNoRelation(Long attrgroupId, Map<String, Object> params);

    List<AttrGroupAndAttrVo> getGroupAndAttr(Long catelogId);

    PageUtils queryPage(Map<String, Object> params, Long catelogId);

    List<AttrGroupAndAttrVo> getAttrGroupWithAttrByCatelogId(Long catelogId);

    List<SpuItemAttrGroup> getAttrGroupWithAttrsBySpuId(Long spuId, Long catalogId);
}

