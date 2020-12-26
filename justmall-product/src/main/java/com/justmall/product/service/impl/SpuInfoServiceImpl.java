package com.justmall.product.service.impl;

import com.alibaba.fastjson.TypeReference;
import com.justmall.common.to.es.SkuEsModel;
import com.justmall.common.to.es.SkuHasStockVo;
import com.justmall.common.utils.R;
import com.justmall.common.constant.ProductConstant;
import com.justmall.product.dao.SpuInfoDescDao;
import com.justmall.product.entity.*;
import com.justmall.product.entity.vo.*;
import com.justmall.product.feign.CouponFeignService;
import com.justmall.product.feign.SearchFeignService;
import com.justmall.product.feign.WareFeignService;
import com.justmall.product.service.*;
import com.justmall.common.to.SkuReductionTo;
import com.justmall.common.to.SpuBoundsTo;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.justmall.common.utils.PageUtils;
import com.justmall.common.utils.Query;

import com.justmall.product.dao.SpuInfoDao;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;


@Service("spuInfoService")
public class SpuInfoServiceImpl extends ServiceImpl<SpuInfoDao, SpuInfoEntity> implements SpuInfoService {

    @Resource
    private SpuInfoDescDao spuInfoDescDao;

    @Autowired
    private SpuImagesService spuImagesService;

    @Resource
    private ProductAttrValueService productAttrValueService;

    @Autowired
    private AttrService attrService;

    @Autowired
    private ProductAttrValueService attrValueService;

    @Autowired
    private SkuInfoService skuInfoService;

    @Autowired
    private SkuImagesService skuImagesService;

    @Autowired
    private SkuSaleAttrValueService skuSaleAttrValueService;

    @Autowired
    private BrandService brandService;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private WareFeignService wareFeignService;

    /**
     * feign 远程调用优惠券服务
     */
    @Autowired
    private CouponFeignService couponFeignService;

    @Autowired
    private SearchFeignService searchFeignService;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<SpuInfoEntity> page = this.page(
                new Query<SpuInfoEntity>().getPage(params),
                new QueryWrapper<SpuInfoEntity>()
        );

        return new PageUtils(page);
    }

    /**
     * 保存spuInfo信息
     *
     * @param spuSaveVo
     */
    @Transactional
    @Override
    public void saveSpuInfo(SpuSaveVo spuSaveVo) {
        SpuInfoEntity spuInfoEntity = new SpuInfoEntity();
        BeanUtils.copyProperties(spuSaveVo, spuInfoEntity);
        // 一、保存基础spu信息
        baseMapper.insert(spuInfoEntity);
        // 二、保存spu描述图片
        SpuInfoDescEntity spuInfoDescEntity = new SpuInfoDescEntity();
        spuInfoDescEntity.setSpuId(spuInfoEntity.getId());
        List<String> decripts = spuSaveVo.getDecript();
        spuInfoDescEntity.setDecript(String.join(",", decripts));
        spuInfoDescDao.insert(spuInfoDescEntity);
        // 三、保存spu图片集
        List<String> images = spuSaveVo.getImages();
        if (images != null && images.size() > 0) {
            List<SpuImagesEntity> imagesEntityList = images.stream().map(img -> {
                SpuImagesEntity spuImagesEntity = new SpuImagesEntity();
                spuImagesEntity.setSpuId(spuInfoEntity.getId());
                spuImagesEntity.setImgUrl(img);
                return spuImagesEntity;
            }).collect(Collectors.toList());
            spuImagesService.saveBatch(imagesEntityList);
        }
        // 四、保存spu的规格参数
        List<BaseAttrs> baseAttrs = spuSaveVo.getBaseAttrs();
        if (baseAttrs != null && baseAttrs.size() > 0) {
            List<ProductAttrValueEntity> productAttrValueEntities = baseAttrs.stream().map(baseAttr -> {
                ProductAttrValueEntity productAttrValueEntity = new ProductAttrValueEntity();
                productAttrValueEntity.setAttrId(baseAttr.getAttrId());
                productAttrValueEntity.setAttrValue(baseAttr.getAttrValues());
                productAttrValueEntity.setQuickShow(baseAttr.getShowDesc());
                AttrEntity attrEntity = attrService.getById(baseAttr.getAttrId());
                productAttrValueEntity.setAttrName(attrEntity.getAttrName());
                productAttrValueEntity.setSpuId(spuInfoEntity.getId());
                return productAttrValueEntity;
            }).collect(Collectors.toList());
            productAttrValueService.saveBatch(productAttrValueEntities);
        }
        // 五、保存spu的积分信息 gulimall_sms sms_spu_bounds
        Bounds bounds = spuSaveVo.getBounds();
        SpuBoundsTo spuBoundsTo = new SpuBoundsTo();
        BeanUtils.copyProperties(bounds, spuBoundsTo);
        spuBoundsTo.setSpuId(spuInfoEntity.getId());
        R result1 = couponFeignService.saveSpuBounds(spuBoundsTo);
        if (result1.getCode() != ProductConstant.AttrEnum.SUCCESS_FEIGN.getCode()) {
            log.error("保存spu积分信息失败");
        }
        // 六、保存spu的所有sku信息
        List<Skus> skus = spuSaveVo.getSkus();
        if (skus != null && skus.size() > 0) {
            // 获取默认图片路径
            skus.stream().forEach(sku -> {
                String default_img = "";
                for (Images image : sku.getImages()) {
                    if (image.getDefaultImg() == ProductConstant.AttrEnum.DEFAULT_IMG.getCode()) {
                        default_img = image.getImgUrl();
                    }
                }
                SkuInfoEntity skuInfoEntity = new SkuInfoEntity();
                BeanUtils.copyProperties(sku, skuInfoEntity);
                skuInfoEntity.setBrandId(spuInfoEntity.getBrandId());
                skuInfoEntity.setCatalogId(spuInfoEntity.getCatalogId());
                skuInfoEntity.setSaleCount(0L);
                skuInfoEntity.setSpuId(spuInfoEntity.getId());
                skuInfoEntity.setSkuDefaultImg(default_img);
                // 1. sku基本信息 pms_sku_info
                skuInfoService.save(skuInfoEntity);
                // skuId
                Long skuId = skuInfoEntity.getSkuId();
                List<SkuImagesEntity> imagesEntityList = sku.getImages().stream().map(img -> {
                    SkuImagesEntity skuImagesEntity = new SkuImagesEntity();
                    skuImagesEntity.setSkuId(skuId);
                    skuImagesEntity.setDefaultImg(img.getDefaultImg());
                    skuImagesEntity.setImgUrl(img.getImgUrl());
                    return skuImagesEntity;
                }).filter(item -> {
                    // 只保存有图片路径的
                    String imgUrl = item.getImgUrl();
                    return (!StringUtils.isEmpty(imgUrl));
                }).collect(Collectors.toList());
                // 2. sku的图片信息 pms_sku_images
                skuImagesService.saveBatch(imagesEntityList);
                List<Attr> attr = sku.getAttr();
                List<SkuSaleAttrValueEntity> skuSaleAttrValueEntityList = attr.stream().map(a -> {
                    SkuSaleAttrValueEntity skuSaleAttrValueEntity = new SkuSaleAttrValueEntity();
                    BeanUtils.copyProperties(a, skuSaleAttrValueEntity);
                    skuSaleAttrValueEntity.setSkuId(skuId);
                    return skuSaleAttrValueEntity;
                }).collect(Collectors.toList());
                // 3. sku的属性信息 pms_sku_sale_attr_value
                skuSaleAttrValueService.saveBatch(skuSaleAttrValueEntityList);
                // 4. sku的优惠满减信息 gulimall_sms  sms_sku_full_reduction / sms_sku_ladder / sms_member_price
                SkuReductionTo skuReductionTo = new SkuReductionTo();
                BeanUtils.copyProperties(sku, skuReductionTo);
                skuReductionTo.setSkuId(skuId);
                if (skuReductionTo.getFullCount() > 0 || skuReductionTo.getFullPrice().compareTo(new BigDecimal("0")) == 1) {
                    R result2 = couponFeignService.saveSkuReduction(skuReductionTo);
                    if (result2.getCode() != ProductConstant.AttrEnum.SUCCESS_FEIGN.getCode()) {
                        log.error("远程的sku优惠满减信息保存失败");
                    }
                }
            });
        }
    }

    @Override
    public void saveBatchSpuInfo(SpuInfoEntity spuInfoEntity) {
        this.baseMapper.insert(spuInfoEntity);
    }

    /**
     * 分页查询带条件
     *
     * @param params
     * @return
     */
    @Override
    public PageUtils queryByCondition(Map<String, Object> params) {
        QueryWrapper<SpuInfoEntity> wrapper = new QueryWrapper<>();
        String key = (String) params.get("key");
        if (!StringUtils.isEmpty(key)) {
            wrapper.and((w) -> {
                w.like("spu_name", key).or().like("spu_description", key);
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
        String status = (String) params.get("status");
        if (!StringUtils.isEmpty(status)) {
            wrapper.eq("publish_status", status);
        }
        IPage<SpuInfoEntity> page = this.page(
                new Query<SpuInfoEntity>().getPage(params),
                wrapper
        );
        return new PageUtils(page);
    }

    /**
     * 不一样的属性：skuPrice、skuImg、hasStock、hotScore、
     * 			brandName、brandImg、catalogName、attrs
     * @param spuId
     */
    @Override
    public void up(Long spuId) {
        // 1 组装数据 查出当前spuId对应的所有sku信息
        List<SkuInfoEntity> skus = skuInfoService.getSkusBySpuId(spuId);
        // 查询这些sku是否有库存
        List<Long> skuids = skus.stream().map(sku -> sku.getSkuId()).collect(Collectors.toList());
        // 2 封装每个sku的信息

        // 3.查询当前sku所有可以被用来检索的规格属性
        // 获取所有的spu商品的id 然后查询这些id中那些是可以被检索的 [数据库中目前 4、5、6、11不可检索]
        List<ProductAttrValueEntity> baseAttrs = attrValueService.baseAttrListForSpu(spuId);

        List<Long> attrIds = baseAttrs.stream().map(ProductAttrValueEntity::getAttrId).collect(Collectors.toList());
        // 可检索的id集合
        Set<Long> isSet = new HashSet<>(attrService.selectSearchAttrIds(attrIds));

        // 根据商品id 过滤不可检索的商品 最后映射号检索属性
        List<SkuEsModel.Attrs> attrs = baseAttrs.stream().filter(item -> isSet.contains(item.getAttrId())).map(item -> {
            SkuEsModel.Attrs attr = new SkuEsModel.Attrs();
            BeanUtils.copyProperties(item, attr);
            return attr;
        }).collect(Collectors.toList());

        // skuId 对应 是否有库存
        Map<Long, Boolean> stockMap = null;
        try {
            // 3.1 发送远程调用 库存系统查询是否有库存
            R hasStock = wareFeignService.getSkuHasStock(skuids);
            // 构造器受保护 所以写成内部类对象
            stockMap = hasStock.getData(new TypeReference<List<SkuHasStockVo>>(){}).stream().collect(Collectors.toMap(SkuHasStockVo::getSkuId, item -> item.getHasStock()));
            log.warn("服务调用成功" + hasStock);
        } catch (Exception e) {
            log.error("库存服务调用失败: 原因{}",e);
        }

        Map<Long, Boolean> finalStockMap = stockMap;
        List<SkuEsModel> collect = skus.stream().map(sku -> {
            SkuEsModel esModel = new SkuEsModel();
            BeanUtils.copyProperties(sku, esModel);
            esModel.setSkuPrice(sku.getPrice());
            esModel.setSkuImg(sku.getSkuDefaultImg());
            // 4 设置库存
            if(finalStockMap == null){
                esModel.setHasStock(true);
            }else {
                esModel.setHasStock(finalStockMap.get(sku.getSkuId()));
            }
            // TODO 1.热度评分 0
            esModel.setHotScore(0L);

            BrandEntity brandEntity = brandService.getById(esModel.getBrandId());

            // brandName、brandImg
            esModel.setBrandName(brandEntity.getName());
            esModel.setBrandImg(brandEntity.getLogo());

            // 查询分类信息
            CategoryEntity categoryEntity = categoryService.getById(esModel.getCatalogId());
            esModel.setCatalogName(categoryEntity.getName());

            // 保存商品的属性
            esModel.setAttrs(attrs);
            return esModel;
        }).collect(Collectors.toList());

        // 5.发给ES进行保存  mall-search
        R r = searchFeignService.productStatusUp(collect);
        if(r.getCode() == 0){
            // 远程调用成功
            baseMapper.updateSpuStatus(spuId, ProductConstant.StatusEnum.SPU_UP.getCode());
        }else{
            // 远程调用失败 TODO 接口幂等性 重试机制
            /**
             * Feign 的调用流程  Feign有自动重试机制
             * 1. 发送请求执行
             * 2.
             */
        }
    }

    @Override
    public SpuInfoEntity getSpuInfoBySkuId(Long skuId) {

        return getById(skuInfoService.getById(skuId).getSpuId());
    }
}