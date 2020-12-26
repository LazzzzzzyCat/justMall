package com.justmall.product.service.impl;

import com.justmall.product.dao.AttrAttrgroupRelationDao;
import com.justmall.product.dao.AttrDao;
import com.justmall.product.dao.CategoryDao;
import com.justmall.product.entity.AttrAttrgroupRelationEntity;
import com.justmall.product.entity.AttrEntity;
import com.justmall.product.entity.CategoryEntity;
import com.justmall.product.entity.vo.AttrGroupAndAttrVo;
import com.justmall.product.entity.vo.AttrGroupRelationVo;
import com.justmall.product.entity.vo.SpuItemAttrGroup;
import com.justmall.product.service.AttrService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.justmall.common.utils.PageUtils;
import com.justmall.common.utils.Query;

import com.justmall.product.dao.AttrGroupDao;
import com.justmall.product.entity.AttrGroupEntity;
import com.justmall.product.service.AttrGroupService;

import javax.annotation.Resource;


@Service("attrGroupService")
public class AttrGroupServiceImpl extends ServiceImpl<AttrGroupDao, AttrGroupEntity> implements AttrGroupService {

    @Resource
    private AttrAttrgroupRelationDao attrAttrgroupRelationDao;

    @Resource
    private AttrDao attrDao;

    @Resource
    private CategoryDao categoryDao;

    @Resource
    private AttrService attrService;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<AttrGroupEntity> page = this.page(
                new Query<AttrGroupEntity>().getPage(params),
                new QueryWrapper<AttrGroupEntity>()
        );

        return new PageUtils(page);
    }

    /**
     * 根据条件和catelogId分页查询分组信息
     *
     * @param params
     * @param catelogId
     * @return
     */
    @Override
    public PageUtils queryPageByCid(Map<String, Object> params, Long catelogId) {
        QueryWrapper<AttrGroupEntity> wrapper = new QueryWrapper<>();
        String key = (String) params.get("key");
        if (!StringUtils.isEmpty(key)) {
            wrapper.and((obj) -> {
                obj.eq("attr_group_id", key).or().like("attr_group_name", key);
            });
        }
        if (catelogId == 0) {
            IPage<AttrGroupEntity> page = this.page(new Query<AttrGroupEntity>().getPage(params),
                    wrapper);
            return new PageUtils(page);
        } else {
            wrapper.eq("catelog_id", catelogId);
            IPage<AttrGroupEntity> page = this.page(new Query<AttrGroupEntity>().getPage(params),
                    wrapper);
            return new PageUtils(page);
        }
    }

    /**
     * 根据attrGroupId 查询attr
     *
     * @param attrgroupId
     * @return
     */
    @Override
    public List<AttrEntity> getAttrByAttrGroupId(Long attrgroupId) {
        List<AttrAttrgroupRelationEntity> relationEntityList = attrAttrgroupRelationDao.selectList(new QueryWrapper<AttrAttrgroupRelationEntity>()
                .eq("attr_group_id", attrgroupId));
        List<Long> attrIdList = relationEntityList.stream().map(AttrAttrgroupRelationEntity::getAttrId).collect(Collectors.toList());
        List<AttrEntity> attrEntities = null;
        if (attrIdList.size() > 0) {
            attrEntities = attrDao.selectBatchIds(attrIdList);
        }
        return attrEntities;
    }

    /**
     * 根据attrId和attrGroupId删除关联
     *
     * @param attrGroupRelationVo
     */
    @Override
    public void deleteRelation(AttrGroupRelationVo[] attrGroupRelationVo) {
        List<AttrAttrgroupRelationEntity> entityList = Arrays.stream(attrGroupRelationVo).map((item) -> {
            AttrAttrgroupRelationEntity attrAttrgroupRelationEntity = new AttrAttrgroupRelationEntity();
            BeanUtils.copyProperties(item, attrAttrgroupRelationEntity);
            return attrAttrgroupRelationEntity;
        }).collect(Collectors.toList());
        attrAttrgroupRelationDao.deleteBathRelations(entityList);
    }

    /**
     * 获取当前分组未关联的属性
     *
     * @param attrgroupId
     * @param params
     * @return
     */
    @Override
    public PageUtils getNoRelation(Long attrgroupId, Map<String, Object> params) {
        // 只能是当前分类
        AttrGroupEntity attrGroupEntity = baseMapper.selectById(attrgroupId);
        CategoryEntity categoryEntity = null;
        PageUtils pageUtils = null;
        if (attrGroupEntity != null) {
            categoryEntity = categoryDao.selectById(attrGroupEntity.getCatelogId());
            // 没有被其他引用(查询当前分类下的其他分组)
            List<AttrGroupEntity> groupEntityList = baseMapper.selectList(new QueryWrapper<AttrGroupEntity>()
                    .eq("catelog_id", attrGroupEntity.getCatelogId())
                    .ne("attr_group_id", attrgroupId));
            List<Long> groupIdList = groupEntityList.stream().map(AttrGroupEntity::getAttrGroupId).collect(Collectors.toList());
            QueryWrapper<AttrAttrgroupRelationEntity> wrapper1 = new QueryWrapper<>();
            if (groupEntityList.size() > 0) {
                wrapper1.in("attr_group_id", groupIdList);
            }
            // 查询这些分组关联的属性
            List<AttrAttrgroupRelationEntity> relationEntityList = attrAttrgroupRelationDao.selectList(wrapper1);
            List<Long> attrIdList = relationEntityList.stream().map(AttrAttrgroupRelationEntity::getAttrId).collect(Collectors.toList());
            // 获取未被关联的属性的条件
            QueryWrapper<AttrEntity> wrapper2 = new QueryWrapper<AttrEntity>()
                    .eq("catelog_id", attrGroupEntity.getCatelogId());
            if (attrIdList.size() > 0) {
                wrapper2.notIn("attr_id", attrIdList);
            }
            String key = (String) params.get("key");
            if (!StringUtils.isEmpty(key)) {
                wrapper2.and((w) -> {
                    w.eq("attr_id", key).or().like("attr_name", key);
                });
            }
            IPage<AttrEntity> page = attrService.page(new Query<AttrEntity>().getPage(params), wrapper2);
            pageUtils = new PageUtils(page);
        }
        return pageUtils;
    }

    @Override
    public List<AttrGroupAndAttrVo> getGroupAndAttr(Long catelogId) {
        List<AttrGroupEntity> groupEntityList = baseMapper.selectList(new QueryWrapper<AttrGroupEntity>().eq("catelog_id", catelogId));
        List<AttrGroupAndAttrVo> attrVoList = new ArrayList<>(100);
        for (AttrGroupEntity attrGroupEntity : groupEntityList) {
            List<AttrAttrgroupRelationEntity> attrAttrgroupRelationEntityList = attrAttrgroupRelationDao.selectList(new QueryWrapper<AttrAttrgroupRelationEntity>()
                    .eq("attr_group_id", attrGroupEntity.getAttrGroupId()));
            List<Long> attrIds = attrAttrgroupRelationEntityList.stream().map(AttrAttrgroupRelationEntity::getAttrId).collect(Collectors.toList());
            List<AttrEntity> attrEntities = null;
            if (attrIds.size() > 0) {
                attrEntities = attrDao.selectBatchIds(attrIds);
            }
            AttrGroupAndAttrVo attrGroupAndAttrVo = new AttrGroupAndAttrVo();
            BeanUtils.copyProperties(attrGroupEntity, attrGroupAndAttrVo);
            attrGroupAndAttrVo.setAttrs(attrEntities);
            attrVoList.add(attrGroupAndAttrVo);
        }
        return attrVoList;
    }

    @Override
    public PageUtils queryPage(Map<String, Object> params, Long catelogId) {
        String key = (String) params.get("key");
        QueryWrapper<AttrGroupEntity> wrapper = new QueryWrapper<>();
        if(!StringUtils.isEmpty(key)){
            wrapper.and((obj)->
                    obj.eq("attr_group_id", key).or().like("attr_group_name", key)
            );
        }
        if(catelogId == 0){
            IPage<AttrGroupEntity> page = this.page(new Query<AttrGroupEntity>().getPage(params),
                    wrapper);
            return new PageUtils(page);
        }else {
            wrapper.eq("catelog_id",catelogId);
            IPage<AttrGroupEntity> page = this.page(new Query<AttrGroupEntity>().getPage(params),
                    wrapper);
            return new PageUtils(page);
        }
    }

    /**
     * 根据分类id 查出所有的分组以及这些组里边的属性
     */
    @Override
    public List<AttrGroupAndAttrVo> getAttrGroupWithAttrByCatelogId(Long catelogId) {

        // 1.查询这个品牌id下所有分组
        List<AttrGroupEntity> attrGroupEntities = this.list(new QueryWrapper<AttrGroupEntity>().eq("catelog_id", catelogId));

        // 2.查询所有属性
        return attrGroupEntities.stream().map(group ->{
            // 先对拷分组数据
            AttrGroupAndAttrVo attrVo = new AttrGroupAndAttrVo();
            BeanUtils.copyProperties(group, attrVo);
            // 按照分组id查询所有关联属性并封装到vo
            List<AttrEntity> attrs = attrService.getRelationAttr(attrVo.getAttrGroupId());
            attrVo.setAttrs(attrs);
            return attrVo;
        }).collect(Collectors.toList());
    }

    @Override
    public List<SpuItemAttrGroup> getAttrGroupWithAttrsBySpuId(Long spuId, Long catalogId) {

        // 1.出当前Spu对应的所有属性的分组信息 以及当前分组下所有属性对应的值
        // 1.1 查询所有分组
        AttrGroupDao baseMapper = this.getBaseMapper();

        return baseMapper.getAttrGroupWithAttrsBySpuId(spuId, catalogId);
    }

}