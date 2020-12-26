package com.justmall.search.service;

import com.justmall.common.to.es.SkuEsModel;

import java.io.IOException;
import java.util.List;

/**
 * <p>Title: ProductSaveService</p>
 * Description：
 * date：2020/12/28 21:15
 */
public interface ProductSaveService {


	boolean productStatusUp(List<SkuEsModel> skuEsModels) throws IOException;
}
