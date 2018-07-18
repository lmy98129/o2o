package com.imooc.o2o.dao;

import java.util.List;

import org.apache.ibatis.annotations.Param;
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
	 * @param shopCondition
	 * @param pageIndex
	 * @param pageSize
	 * @return
	 */
	public List<Shop> getShopList(@Param("shopCondition") Shop shopCondition, @Param("pageIndex") int pageIndex, @Param("pageSize") int pageSize);

}
