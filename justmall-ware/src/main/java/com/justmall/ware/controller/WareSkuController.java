package com.justmall.ware.controller;

import java.util.Arrays;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.justmall.ware.entity.WareSkuEntity;
import com.justmall.ware.service.WareSkuService;
import com.justmall.common.utils.PageUtils;
import com.justmall.common.utils.R;



/**
 * 商品库存
 *
 * @author huwj
 * @email 470661893@qq.com
 * @date 2020-12-20 10:56:39
 */
@RestController
@RequestMapping("ware/waresku")
public class WareSkuController {
    @Autowired
    private WareSkuService wareSkuService;

    /**
     * 列表
     */
    @RequestMapping("/list")
    //@RequiresPermissions("ware:wmswaresku:list")
    public R list(@RequestParam Map<String, Object> params){
        PageUtils page = wareSkuService.queryPage(params);

        return R.ok().put("page", page);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{id}")
    //@RequiresPermissions("ware:wmswaresku:info")
    public R info(@PathVariable("id") Long id){
		WareSkuEntity wmsWareSku = wareSkuService.getById(id);

        return R.ok().put("wmsWareSku", wmsWareSku);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    //@RequiresPermissions("ware:wmswaresku:save")
    public R save(@RequestBody WareSkuEntity wmsWareSku){
		wareSkuService.save(wmsWareSku);

        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    //@RequiresPermissions("ware:wmswaresku:update")
    public R update(@RequestBody WareSkuEntity wmsWareSku){
		wareSkuService.updateById(wmsWareSku);

        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    //@RequiresPermissions("ware:wmswaresku:delete")
    public R delete(@RequestBody Long[] ids){
		wareSkuService.removeByIds(Arrays.asList(ids));

        return R.ok();
    }

}
