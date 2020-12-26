package com.justmall.product.service.impl;

import com.alibaba.fastjson.TypeReference;
import com.justmall.common.utils.R;
import com.justmall.product.entity.SkuImagesEntity;
import com.justmall.product.entity.SpuInfoDescEntity;
import com.justmall.product.entity.vo.ItemSaleAttrVo;
import com.justmall.product.entity.vo.SeckillInfoVo;
import com.justmall.product.entity.vo.SkuItemVo;
import com.justmall.product.entity.vo.SpuItemAttrGroup;
import com.justmall.product.feign.SeckillFeignService;
import com.justmall.product.service.*;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ThreadPoolExecutor;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.justmall.common.utils.PageUtils;
import com.justmall.common.utils.Query;

import com.justmall.product.dao.SkuInfoDao;
import com.justmall.product.entity.SkuInfoEntity;


@Service("skuInfoService")
public class SkuInfoServiceImpl extends ServiceImpl<SkuInfoDao, SkuInfoEntity> implements SkuInfoService {

    @Autowired
    private SkuImagesService imagesService;

    @Autowired
    private SpuInfoDescService spuInfoDescService;

    @Autowired
    private AttrGroupService attrGroupService;

    @Autowired
    private SkuSaleAttrValueService skuSaleAttrValueService;

    @Autowired
    private SeckillFeignService seckillFeignService;

    /**
     * 自定义线程串池
     */
    @Autowired
    private ThreadPoolExecutor executor;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<SkuInfoEntity> page = this.page(
                new Query<SkuInfoEntity>().getPage(params),
                new QueryWrapper<SkuInfoEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public PageUtils getSkuByCondition(Map<String, Object> params) {
        QueryWrapper<SkuInfoEntity> wrapper = new QueryWrapper<>();
        String key = (String) params.get("key");
        if (!StringUtils.isEmpty(key)) {
            wrapper.and(w -> {
                w.like("sku_name", key).or().like("sku_title", key).or().like("sku_subtitle", key);
            });
        }
        String catelogId = (String) params.get("catelogId");
        if (!StringUtils.isEmpty(catelogId) && !"0".equalsIgnoreCase(catelogId)) {
            wrapper.eq("catelog_id", catelogId);
        }
        String brandId = (String) params.get("brandId");
        if (!StringUtils.isEmpty(brandId) && !"0".equalsIgnoreCase(brandId)) {
            wrapper.eq("brand_id", brandId);
        }
        String min = (String) params.get("min");
        String max = (String) params.get("max");
        if (!StringUtils.isEmpty(max) && !StringUtils.isEmpty(min)) {
            try {
                BigDecimal bigDecimalMin = new BigDecimal(min);
                BigDecimal bigDecimalMax = new BigDecimal(max);
                if (bigDecimalMax.compareTo(bigDecimalMin) == 1) {
                    wrapper.ge("price", min);
                    if (bigDecimalMax.compareTo(new BigDecimal("0")) > 0) {
                        wrapper.lt("price", max);
                    }
                } else {
                    wrapper.ge("price", max);
                    if (bigDecimalMin.compareTo(new BigDecimal("0")) > 0) {
                        wrapper.lt("price", min);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        IPage<SkuInfoEntity> page = this.page(
                new Query<SkuInfoEntity>().getPage(params),
                wrapper
        );

        return new PageUtils(page);
    }

    @Override
    public List<SkuInfoEntity> getSkusBySpuId(Long spuId) {
        return this.list(new QueryWrapper<SkuInfoEntity>().eq("spu_id", spuId));
    }

    /**
     * 查询页面详细内容
     */
    @Override
    public SkuItemVo item(Long skuId) throws ExecutionException, InterruptedException {
        SkuItemVo skuItemVo = new SkuItemVo();

        CompletableFuture<SkuInfoEntity> infoFutrue = CompletableFuture.supplyAsync(() -> {
            //1 sku基本信息
            SkuInfoEntity info = getById(skuId);
            skuItemVo.setInfo(info);
            return info;
        }, executor);

        CompletableFuture<Void> ImgageFuture = CompletableFuture.runAsync(() -> {
            //2 sku图片信息
            List<SkuImagesEntity> images = imagesService.getImagesBySkuId(skuId);
            skuItemVo.setImages(images);
        }, executor);

        CompletableFuture<Void> saleAttrFuture =infoFutrue.thenAcceptAsync(res -> {
            //3 获取spu销售属性组合
            List<ItemSaleAttrVo> saleAttrVos = skuSaleAttrValueService.getSaleAttrsBuSpuId(res.getSpuId());
            skuItemVo.setSaleAttr(saleAttrVos);
        },executor);

        CompletableFuture<Void> descFuture = infoFutrue.thenAcceptAsync(res -> {
            //4 获取spu介绍
            SpuInfoDescEntity spuInfo = spuInfoDescService.getById(res.getSpuId());
            skuItemVo.setDesc(spuInfo);
        },executor);

        CompletableFuture<Void> baseAttrFuture = infoFutrue.thenAcceptAsync(res -> {
            //5 获取spu规格参数信息
            List<SpuItemAttrGroup> attrGroups = attrGroupService.getAttrGroupWithAttrsBySpuId(res.getSpuId(), res.getCatalogId());
            skuItemVo.setGroupAttrs(attrGroups);
        }, executor);

        // 6.查询当前sku是否参与秒杀优惠
        CompletableFuture<Void> secKillFuture = CompletableFuture.runAsync(() -> {
            R skuSeckillInfo = seckillFeignService.getSkuSeckillInfo(skuId);
            if (skuSeckillInfo.getCode() == 0) {
                SeckillInfoVo seckillInfoVo = skuSeckillInfo.getData(new TypeReference<SeckillInfoVo>() {});
                skuItemVo.setSeckillInfoVo(seckillInfoVo);
            }
        }, executor);

        // 等待所有任务都完成再返回
        CompletableFuture.allOf(ImgageFuture,saleAttrFuture,descFuture,baseAttrFuture,secKillFuture).get();
        return skuItemVo;
    }

}