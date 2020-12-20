package com.justmall.member.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.justmall.common.utils.PageUtils;
import com.justmall.member.entity.UmsMemberCollectSpuEntity;

import java.util.Map;

/**
 * 会员收藏的商品
 *
 * @author huwj
 * @email 470661893@qq.com
 * @date 2020-12-20 10:53:32
 */
public interface UmsMemberCollectSpuService extends IService<UmsMemberCollectSpuEntity> {

    PageUtils queryPage(Map<String, Object> params);
}

