package com.justmall.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.justmall.common.utils.PageUtils;
import com.justmall.product.entity.BrandEntity;
import com.justmall.product.entity.CategoryBrandRelationEntity;

import java.util.List;
import java.util.Map;

/**
 * 品牌分类关联
 *
 * @author huwj
 * @email 470661893@qq.com
 * @date 2020-12-20 09:32:34
 */
public interface CategoryBrandRelationService extends IService<CategoryBrandRelationEntity> {


    PageUtils queryPage(Map<String, Object> params);

    List<CategoryBrandRelationEntity> getBrandCateRelation(Long brandId);

    void saveIdAndName(CategoryBrandRelationEntity categoryBrandRelation);

    List<BrandEntity> getBrandByCatlogId(Long catId);
}

