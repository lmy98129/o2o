package com.imooc.o2o.dao;

import java.util.List;

import com.imooc.o2o.entity.Shop;

public interface ShopDao {
	/**
	 * 新增店铺
	 * 
	 * @param shop
	 * @return
	 */
	int insertShop(Shop shop);

	/**
	 * 更新店铺信息
	 * 
	 * @param shop
	 * @return
	 */
	int updateShop(Shop shop);
	
	/**
	 * 通过shopId查找店铺
	 * 
	 * @param shopId
	 * @return
	 */
	Shop findShopById(long shopId);
	
	/**
	 * 分页、多条件查询店铺列表
	 * 
	 * @param startRow 查询起始行
	 * @param pageSize 查询页面长度
	 * @param shopCondition 页面查询条件
	 * @return
	 */
	List<Shop> getShopList(int startRow, int pageSize, Shop shopCondition);
}
