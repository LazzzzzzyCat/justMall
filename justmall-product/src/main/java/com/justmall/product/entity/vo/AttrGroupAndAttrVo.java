package com.justmall.product.entity.vo;


import com.justmall.product.entity.AttrEntity;
import com.justmall.product.entity.AttrGroupEntity;
import lombok.Data;

import java.util.List;

@Data
public class AttrGroupAndAttrVo extends AttrGroupEntity {

    /**
     * 属性列表
     */
    private List<AttrEntity> attrs;
}
