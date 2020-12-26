package com.justmall.product.entity.vo;

import lombok.Data;

@Data
public class AttrResVo extends AttrVo {
    /**
     * 属性分组分类名
     */
    private String catelogName;
    /**
     * 属性分组名
     */
    private String groupName;

    /**
     * 属性名称全路径
     */
    private Long[] catelogPath;
}
