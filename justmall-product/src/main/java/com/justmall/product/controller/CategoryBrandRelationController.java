package com.justmall.product.controller;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import com.justmall.product.entity.BrandEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.justmall.product.entity.CategoryBrandRelationEntity;
import com.justmall.product.service.CategoryBrandRelationService;
import com.justmall.common.utils.PageUtils;
import com.justmall.common.utils.R;



/**
 * 品牌分类关联
 *
 * @author huwj
 * @email 470661893@qq.com
 * @date 2020-12-20 10:05:59
 */
@RestController
@RequestMapping("product/categorybrandrelation")
public class CategoryBrandRelationController {
    @Autowired
    private CategoryBrandRelationService categoryBrandRelationService;

    /**
     * 获取catId下的品牌
     */
    @GetMapping("/brands/list")
    public R getBrandByCatId(@RequestParam Long catId) {
        List<BrandEntity> list = categoryBrandRelationService.getBrandByCatlogId(catId);
        return R.ok().put("data", list);
    }

    /**
     * 获取品牌关联的分类
     */
    @GetMapping("/catelog/list")
    public R getBrandCateRelation(@RequestParam Long brandId) {
        List<CategoryBrandRelationEntity> attrBrandCateRelationVos = categoryBrandRelationService.getBrandCateRelation(brandId);
        return R.ok().put("data", attrBrandCateRelationVos);
    }

    /**
     * 列表
     */
    @RequestMapping("/list")
    public R list(@RequestParam Map<String, Object> params) {
        PageUtils page = categoryBrandRelationService.queryPage(params);
        return R.ok().put("page", page);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{id}")
    public R info(@PathVariable("id") Long id) {
        CategoryBrandRelationEntity categoryBrandRelation = categoryBrandRelationService.getById(id);

        return R.ok().put("categoryBrandRelation", categoryBrandRelation);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    public R save(@RequestBody CategoryBrandRelationEntity categoryBrandRelation) {
        categoryBrandRelationService.saveIdAndName(categoryBrandRelation);
        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    public R update(@RequestBody CategoryBrandRelationEntity categoryBrandRelation) {
        categoryBrandRelationService.updateById(categoryBrandRelation);
        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    public R delete(@RequestBody Long[] ids) {
        categoryBrandRelationService.removeByIds(Arrays.asList(ids));
        return R.ok();
    }

}
