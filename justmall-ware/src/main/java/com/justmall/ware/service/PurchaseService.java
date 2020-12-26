package com.justmall.ware.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.justmall.common.utils.PageUtils;
import com.justmall.ware.entity.PurchaseEntity;
import com.justmall.ware.entity.vo.DoneReqVo;
import com.justmall.ware.entity.vo.MergeReqVo;

import java.util.List;
import java.util.Map;

/**
 * 采购信息
 *
 * @author huwj
 * @email 470661893@qq.com
 * @date 2020-12-20 10:56:39
 */
public interface PurchaseService extends IService<PurchaseEntity> {

    PageUtils queryPage(Map<String, Object> params);

    PageUtils getUnclaimedPurchase(Map<String, Object> params);

    void mergePurchase(MergeReqVo mergeReqVo);

    void receivePurchase(List<Long> ids);

    void done(DoneReqVo doneReqVo);
}

