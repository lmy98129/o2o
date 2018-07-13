package com.imooc.o2o.service;

import java.util.List;
import com.imooc.o2o.entity.ShopCategory;

public interface ShopCategoryService {
	/**
	 * 列出商店类别中的顶级类别列表
	 * 
	 * @return shopCategoryList
	 */
	List<ShopCategory> findTopShopCategories();
	/**
	 * 根据查询条件获取ShopCategory列表
	 * 
	 * @param shopCategoryCondition
	 * @return
	 */
	List<ShopCategory> getShopCategoryList(ShopCategory shopCategoryCondition);
}
