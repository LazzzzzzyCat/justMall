package com.justmall.search.service;

import com.justmall.search.vo.SearchParam;
import com.justmall.search.vo.SearchResult;

/**
 * <p>Title: MasllService</p>
 * Description：
 * date：2020/6/12 23:05
 */
public interface JustMallService {

	/**
	 * 检索所有参数
	 */
	SearchResult search(SearchParam Param);
}
