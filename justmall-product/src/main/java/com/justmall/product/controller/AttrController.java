package com.justmall.product.controller;

import com.justmall.common.utils.PageUtils;
import com.justmall.common.utils.R;
import com.justmall.product.entity.ProductAttrValueEntity;
import com.justmall.product.entity.vo.AttrResVo;
import com.justmall.product.entity.vo.AttrVo;
import com.justmall.product.service.AttrService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;
import java.util.Map;



/**
 * 商品属性
 *
 * @author huwj
 * @email 470661893@qq.com
 * @date 2020-12-20 10:05:59
 */
@RestController
@RequestMapping("product/attr")
public class AttrController {
    @Autowired
    private AttrService attrService;

    @PostMapping("/update/{spuId}")
    public R updateSpecification(@PathVariable("spuId") Long spuId,
                                 @RequestBody List<ProductAttrValueEntity> productAttrValueEntities) {
        attrService.updateSpecification(spuId, productAttrValueEntities);
        return R.ok();
    }

    /**
     * 获取spu规格
     *
     * @return
     */
    @GetMapping("/base/listforspu/{spuId}")
    public R getSpuSpecification(@PathVariable("spuId") Long spuId) {
        List<ProductAttrValueEntity> spuSpecification = attrService.getSpuSpecification(spuId);
        return R.ok().put("data", spuSpecification);
    }

    /**
     * 列表
     */
    @RequestMapping("/list")
    public R list(@RequestParam Map<String, Object> params) {
        PageUtils page = attrService.queryPage(params);

        return R.ok().put("page", page);
    }

    /**
     * 列表
     */
    @RequestMapping("/{attrType}/list/{catelogId}")
    public R list(@RequestParam Map<String, Object> params,
                  @PathVariable("catelogId") Long catelogId,
                  @PathVariable("attrType") String attrType) {
        PageUtils page = attrService.queryByCid(params, catelogId, attrType);

        return R.ok().put("page", page);
    }

    /**
     * 信息
     */
    @GetMapping("/info/{attrId}")
    public R info(@PathVariable("attrId") Long attrId) {
        AttrResVo attr = attrService.getAttrInfo(attrId);
        return R.ok().put("attr", attr);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    public R save(@RequestBody AttrVo attr) {
        attrService.saveVo(attr);
        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    public R update(@RequestBody AttrVo attr) {
        attrService.updateVo(attr);
        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    public R delete(@RequestBody Long[] attrIds) {
        attrService.removeByIds(Arrays.asList(attrIds));
        return R.ok();
    }

}
