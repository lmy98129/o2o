package com.imooc.o2o.dao;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.imooc.o2o.entity.Product;

public interface ProductDao {
	
	/**
	 * 将商品与商品类别的关系进行解除
	 * @param productCategoryId
	 * @return
	 */
	int setProductCategoryNull(long productCategoryId);
	
	/**
	 * 查询指定某个店铺下的所有商品类别信息
	 * 
	 * @param productCondition
	 * @param pageIndex
	 * @param pageSize
	 * @return
	 */
	List<Product> getProductList(@Param("productCondition") Product productCondition,@Param("rowIndex") int pageIndex, @Param("pageSize") int pageSize);
	
	/**
	 * 通过商品Id查询唯一的商品信息
	 * @param productId
	 * @return
	 */
	Product getProductById(long productId);
	
	/**
	 * 更新商品信息
	 * 
	 * @param product
	 * @return
	 */
	int updateProduct(Product product);
	
	/**
	 * 添加商品信息
	 * 
	 * @param product
	 * @return
	 */
	int insertProduct(Product product);
}
