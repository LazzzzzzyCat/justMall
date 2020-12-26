package com.justmall.common.to.es;

import lombok.Data;

/**
 * <p>Title: SkuHasStockVo</p>
 * Description：存储这个sku是否有库存
 * date：2020/12/28 20:10
 */
@Data
public class SkuHasStockVo {

	private Long skuId;

	private Boolean hasStock;
}
