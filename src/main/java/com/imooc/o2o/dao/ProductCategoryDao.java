package com.imooc.o2o.dao;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.imooc.o2o.entity.ProductCategory;

public interface ProductCategoryDao {
	/**
	 * 查询指定某个店铺下的所有商品类别信息
	 * 
	 * @param shopId
	 * @return
	 */
	List<ProductCategory> queryProductCategoryList(long shopId);
	
	/**
	 * 批量插入商品类别数据
	 * 
	 * @param productCategoryList
	 * @return
	 */
	int batchAddProductCategory(List<ProductCategory> productCategoryList);
	
	/**
	 * 将此类别下的商品里的类别id置为空，再删除掉该商品类别
	 * 
	 * @param productCategoryId
	 * @param shopId
	 * @return
	 */
	int deleteProductCategory(@Param("productCategoryId") long productCategoryId, @Param("shopId") long shopId);
}
