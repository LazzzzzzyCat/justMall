package com.justmall.ware.controller;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import com.justmall.ware.entity.vo.DoneReqVo;
import com.justmall.ware.entity.vo.MergeReqVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.justmall.ware.entity.PurchaseEntity;
import com.justmall.ware.service.PurchaseService;
import com.justmall.common.utils.PageUtils;
import com.justmall.common.utils.R;



/**
 * 采购信息
 *
 * @author huwj
 * @email 470661893@qq.com
 * @date 2020-12-20 10:56:39
 */
@RestController
@RequestMapping("ware/purchase")
public class PurchaseController {
    @Autowired
    private PurchaseService purchaseService;

    /**
     * 完成采购
     */
    @PostMapping("/done")
    public R finishPurchase(@RequestBody DoneReqVo doneReqVo) {
        purchaseService.done(doneReqVo);
        return R.ok();
    }

    /**
     * 领取采购单
     */
    @PostMapping("/received")

    public R receivePurchase(@RequestParam List<Long> ids) {
        purchaseService.receivePurchase(ids);
        return R.ok();
    }

    /**
     * 合并采购单
     */
    @PostMapping("/merge")
    public R mergePurchase(@RequestBody MergeReqVo mergeReqVo) {
        purchaseService.mergePurchase(mergeReqVo);
        return R.ok();
    }

    /**
     * 查询未领取的采购单
     */
    @GetMapping("/unreceive/list")
    public R getUnclaimedPurchase(Map<String, Object> params) {
        PageUtils pageUtils = purchaseService.getUnclaimedPurchase(params);
        return R.ok().put("page", pageUtils);
    }

    /**
     * 列表
     */
    @RequestMapping("/list")
    public R list(@RequestParam Map<String, Object> params) {
        PageUtils page = purchaseService.queryPage(params);

        return R.ok().put("page", page);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{id}")
    public R info(@PathVariable("id") Long id) {
        PurchaseEntity purchase = purchaseService.getById(id);

        return R.ok().put("purchase", purchase);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    public R save(@RequestBody PurchaseEntity purchase) {
        purchaseService.save(purchase);
        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    public R update(@RequestBody PurchaseEntity purchase) {
        purchaseService.updateById(purchase);

        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    public R delete(@RequestBody Long[] ids) {
        purchaseService.removeByIds(Arrays.asList(ids));

        return R.ok();
    }
}
