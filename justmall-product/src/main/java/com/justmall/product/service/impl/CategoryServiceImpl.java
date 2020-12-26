package com.justmall.product.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.justmall.common.exception.RRException;
import com.justmall.common.utils.PageUtils;
import com.justmall.common.utils.Query;
import com.justmall.product.dao.CategoryDao;
import com.justmall.product.entity.CategoryEntity;
import com.justmall.product.service.CategoryService;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;


@Service("categoryService")
public class CategoryServiceImpl extends ServiceImpl<CategoryDao, CategoryEntity> implements CategoryService {

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<CategoryEntity> page = this.page(
                new Query<CategoryEntity>().getPage(params),
                new QueryWrapper<CategoryEntity>()
        );

        return new PageUtils(page);
    }

    /**
     * 树型显示
     *
     * @return
     */
    @Override
    public List<CategoryEntity> listWithTree() {
        List<CategoryEntity> categoryEntities = baseMapper.selectList(null);

        return categoryEntities.stream().filter((categoryEntity) -> {
            return categoryEntity.getParentCid() == 0;
        }).sorted(Comparator.comparingInt(menu -> (menu.getSort() == null ? 0 : menu.getSort()))).map((menu) -> {
            menu.setChildren(getChildren(menu, categoryEntities));
            return menu;
        }).collect(Collectors.toList());
    }


    private List<CategoryEntity> getChildren(CategoryEntity rootMenu, List<CategoryEntity> allMenus) {

        return allMenus.stream().filter(categoryEntity -> categoryEntity.getParentCid().equals(rootMenu.getCatId())).map(menu -> {
            menu.setChildren(getChildren(menu, allMenus));
            return menu;
        }).sorted(Comparator.comparingInt(m -> (m.getSort() == null ? 0 : m.getSort()))).collect(Collectors.toList());
    }

    @Override
    public void removeMenus(List<Long> asList) {
        System.out.println("删除的数据为： ++ ++++" + asList);
        // 逻辑删除
        int result = baseMapper.deleteBatchIds(asList);
        if (result < 0) {
            throw new RRException("删除失败");
        }
    }

    /**
     * 寻找一个category的全路径  example [2.3,6]
     *
     * @param catelogId
     * @return
     */
    @Override
    public Long[] findCategoryPath(Long catelogId) {
        List<Long> path = new ArrayList<>();
        List<Long> parentPath = findParentPath(catelogId, path);
        Collections.reverse(parentPath);
        return parentPath.toArray(new Long[parentPath.size()]);
    }

    private List<Long> findParentPath(Long catelogId, List<Long> path) {
        path.add(catelogId);
        CategoryEntity categoryEntity = baseMapper.selectById(catelogId);
        if (categoryEntity.getParentCid() != 0) {
            findParentPath(categoryEntity.getParentCid(), path);
        }
        return path;
    }
}