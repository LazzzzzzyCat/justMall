package com.justmall.product.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.justmall.common.utils.PageUtils;
import com.justmall.common.utils.Query;
import com.justmall.common.constant.ProductConstant;
import com.justmall.product.dao.*;
import com.justmall.product.entity.*;
import com.justmall.product.entity.vo.AttrGroupRelationVo;
import com.justmall.product.entity.vo.AttrResVo;
import com.justmall.product.entity.vo.AttrVo;
import com.justmall.product.service.AttrService;
import com.justmall.product.service.ProductAttrValueService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@Service("attrService")
public class AttrServiceImpl extends ServiceImpl<AttrDao, AttrEntity> implements AttrService {

    @Resource
    private AttrAttrgroupRelationDao attrAttrgroupRelationDao;

    @Resource
    private AttrGroupDao attrGroupDao;

    @Resource
    private CategoryDao categoryDao;

    @Resource
    private CategoryServiceImpl categoryService;

    @Resource
    private ProductAttrValueService productAttrValueService;

    @Resource
    private ProductAttrValueDao productAttrValueDao;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<AttrEntity> page = this.page(
                new Query<AttrEntity>().getPage(params),
                new QueryWrapper<AttrEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public PageUtils queryByCid(Map<String, Object> params, Long catelogId, String attrType) {
        QueryWrapper<AttrEntity> wrapper = new QueryWrapper<>();
        String key = (String) params.get("key");
        if (!StringUtils.isEmpty(key)) {
            wrapper.and((obj) -> {
                obj.eq("attr_id", key).or().like("attr_name", key);
            });
        }
        PageUtils pageUtils = null;
        IPage<AttrEntity> page = null;
        if (catelogId != 0) {
            wrapper.eq("catelog_id", catelogId);
        }
        wrapper.eq("attr_type", "base".equalsIgnoreCase(attrType) ? ProductConstant.AttrEnum.ATTR_TYPE_BASE.getCode() : ProductConstant.AttrEnum.ATTR_TYPE_SALE.getCode());
        page = this.page(new Query<AttrEntity>().getPage(params),
                wrapper);
        pageUtils = new PageUtils(page);
        List<AttrEntity> records = page.getRecords();
        // 需要将groupName 和 catelogName 一同返回
        List<AttrResVo> attrResVoList = records.stream().map((attrEntity) -> {
            AttrResVo attrResVo = new AttrResVo();
            BeanUtils.copyProperties(attrEntity, attrResVo);
            AttrAttrgroupRelationEntity relationEntity = attrAttrgroupRelationDao
                    .selectOne(new QueryWrapper<AttrAttrgroupRelationEntity>()
                            .eq("attr_id", attrEntity.getAttrId()));
            if ("base".equalsIgnoreCase(attrType)) {
                if (relationEntity != null && relationEntity.getAttrGroupId() != null) {
                    AttrGroupEntity attrGroupEntity = attrGroupDao.selectById(relationEntity.getAttrGroupId());
                    attrResVo.setGroupName(attrGroupEntity.getAttrGroupName());
                }
            }
            CategoryEntity categoryEntity = categoryDao.selectById(attrEntity.getCatelogId());
            if (categoryEntity != null) {
                attrResVo.setCatelogName(categoryEntity.getName());
            }
            return attrResVo;
        }).collect(Collectors.toList());
        pageUtils.setList(attrResVoList);
        return pageUtils;
    }

    @Transactional
    @Override
    public void saveVo(AttrVo attr) {
        AttrEntity attrEntity = new AttrEntity();
        BeanUtils.copyProperties(attr, attrEntity);
        baseMapper.insert(attrEntity);
        // 保存分组关系
        if (attr.getAttrType() == ProductConstant.AttrEnum.ATTR_TYPE_BASE.getCode() && attr.getAttrGroupId() != null) {
            AttrAttrgroupRelationEntity groupEntity = new AttrAttrgroupRelationEntity();
            groupEntity.setAttrGroupId(attr.getAttrGroupId());
            groupEntity.setAttrId(attrEntity.getAttrId());
            attrAttrgroupRelationDao.insert(groupEntity);
        }
    }

    @Override
    public AttrResVo getAttrInfo(Long attrId) {
        AttrEntity attrEntity = baseMapper.selectById(attrId);
        AttrResVo attrResVo = new AttrResVo();
        BeanUtils.copyProperties(attrEntity, attrResVo);
        AttrAttrgroupRelationEntity relationEntity = attrAttrgroupRelationDao.selectOne(new QueryWrapper<AttrAttrgroupRelationEntity>()
                .eq("attr_id", attrEntity.getAttrId()));
        // 将attrGroupId 和 GroupName 返回
        if (relationEntity != null) {
            attrResVo.setAttrGroupId(relationEntity.getAttrGroupId());
            AttrGroupEntity attrGroupEntity = attrGroupDao.selectById(relationEntity.getAttrGroupId());
            if (attrGroupEntity != null) {
                attrResVo.setGroupName(attrGroupEntity.getAttrGroupName());
            }
        }
        // 将catelogPath和 catlogName 返回
        Long[] categoryPath = categoryService.findCategoryPath(attrEntity.getCatelogId());
        attrResVo.setCatelogPath(categoryPath);
        CategoryEntity categoryEntity = categoryDao.selectById(attrEntity.getCatelogId());
        if (categoryEntity != null) {
            attrResVo.setCatelogName(categoryEntity.getName());
        }
        return attrResVo;
    }

    @Transactional
    @Override
    public void updateVo(AttrVo attr) {
        // 修改基本属性
        AttrEntity attrEntity = new AttrEntity();
        BeanUtils.copyProperties(attr, attrEntity);
        baseMapper.updateById(attrEntity);
        // 修改分组关联
        if (attr.getAttrType() == ProductConstant.AttrEnum.ATTR_TYPE_BASE.getCode()) {
            AttrAttrgroupRelationEntity relationEntity = new AttrAttrgroupRelationEntity();
            relationEntity.setAttrGroupId(attr.getAttrGroupId());
            Integer count = attrAttrgroupRelationDao.selectCount(new QueryWrapper<AttrAttrgroupRelationEntity>()
                    .eq("attr_id", attr.getAttrId()));
            if (count > 0) {
                attrAttrgroupRelationDao.update(relationEntity, new QueryWrapper<AttrAttrgroupRelationEntity>()
                        .eq("attr_id", attrEntity.getAttrId()));
            } else {
                relationEntity.setAttrId(attr.getAttrId());
                attrAttrgroupRelationDao.insert(relationEntity);
            }
        }
    }

    /**
     * 获取spu规格
     *
     * @param spuId
     * @return
     */
    @Override
    public List<ProductAttrValueEntity> getSpuSpecification(Long spuId) {
        return productAttrValueDao.selectList(new QueryWrapper<ProductAttrValueEntity>()
                .eq("spu_id", spuId));
    }

    /**
     * 修改商品规格
     *
     * @param spuId
     * @param productAttrValueEntities
     */
    @Override
    public void updateSpecification(Long spuId, List<ProductAttrValueEntity> productAttrValueEntities) {
        // 删除老数据
        productAttrValueService.remove(new QueryWrapper<ProductAttrValueEntity>()
                .eq("spu_id", spuId));
        // 插入新数据
        List<ProductAttrValueEntity> collect = productAttrValueEntities.stream().peek(item -> item.setSpuId(spuId)).collect(Collectors.toList());
        productAttrValueService.saveBatch(collect);
    }


    /**
     * 根据分组id查找关联属性
     */
    @Override
    public List<AttrEntity> getRelationAttr(Long attrgroupId) {
        List<AttrAttrgroupRelationEntity> entities = attrAttrgroupRelationDao.selectList(new QueryWrapper<AttrAttrgroupRelationEntity>().eq("attr_group_id", attrgroupId));
        List<Long> attrIds = entities.stream().map(AttrAttrgroupRelationEntity::getAttrId).collect(Collectors.toList());
        // 根据这个属性查询到的id可能是空的
        if(attrIds.size() == 0){
            return null;
        }
        return this.listByIds(attrIds);
    }

    /**
     * 批量删除分组关联关系
     */
    @Override
    public void deleteRelation(AttrGroupRelationVo[] vos) {
        // 将页面收集的数据拷到 AttrAttrgroupRelationEntity
        List<AttrAttrgroupRelationEntity> entities = Arrays.asList(vos).stream().map((v) -> {
            AttrAttrgroupRelationEntity relationEntity = new AttrAttrgroupRelationEntity();
            BeanUtils.copyProperties(v, relationEntity);
            return relationEntity;
        }).collect(Collectors.toList());
        attrAttrgroupRelationDao.deleteBathRelations(entities);
    }

    /**
     * 获取当前分组没有关联的属性
     */
    @Override
    public PageUtils getNoRelationAttr(Map<String, Object> params, Long attrgroupId) {
        //1、当前分组只能关联自己所属的分类里面的所有属性
        AttrGroupEntity attrGroupEntity = attrGroupDao.selectById(attrgroupId);
        Long catelogId = attrGroupEntity.getCatelogId();
        // 2、当前分组只能别的分组没有引用的属性																									并且这个分组的id不是我当前正在查的id
        //2.1)、当前分类下的其他分组
        List<AttrGroupEntity> group = attrGroupDao.selectList(new QueryWrapper<AttrGroupEntity>().eq("catelog_id", catelogId));
        // 得到当前分类下面的所有分组id
        List<Long> collect = group.stream().map(AttrGroupEntity::getAttrGroupId).collect(Collectors.toList());

        //2.2)、查询这些分组关联的属性
        List<AttrAttrgroupRelationEntity> groupId = attrAttrgroupRelationDao.selectList(new QueryWrapper<AttrAttrgroupRelationEntity>().in("attr_group_id", collect));
        // 再次获取跟这些分组有关的属性id的集合
        List<Long> attrIds = groupId.stream().map(AttrAttrgroupRelationEntity::getAttrId).collect(Collectors.toList());

        //2.3)、从当前分类的所有属性中移除这些属性；[因这些分组已经存在被选了 就不用再显示了]
        QueryWrapper<AttrEntity> wrapper = new QueryWrapper<AttrEntity>().eq("catelog_id", catelogId).eq("attr_type", ProductConstant.AttrEnum.ATTR_TYPE_BASE.getCode());
        if(attrIds.size() > 0){
            wrapper.notIn("attr_id", attrIds);
        }
        // 当搜索框中有key并且不为空的时候 进行模糊查询
        String key = (String) params.get("key");
        if(!StringUtils.isEmpty(key)){
            wrapper.and((w)->{
                w.eq("attr_id",key).or().like("attr_name",key);
            });
        }
        // 将最后返回的结果进行封装
        IPage<AttrEntity> page = this.page(new Query<AttrEntity>().getPage(params), wrapper);

        return new PageUtils(page);
    }

    /**
     * SELECT attr_id FROM `pms_attr` WHERE attr_id IN (?) AND search_type = 1
     */
    @Override
    public List<Long> selectSearchAttrIds(List<Long> attrIds) {
        return baseMapper.selectSearchAttrIds(attrIds);
    }

}