package com.justmall.product.entity.vo;


import com.justmall.product.entity.AttrEntity;
import lombok.Data;

@Data
public class AttrVo extends AttrEntity {
    /**
     * 属性分组id
     */
    private Long attrGroupId;
}
