package com.imooc.o2o.dao;

public interface ProductDao {
	
	/**
	 * 将商品与商品类别的关系进行解除
	 * @param productCategoryId
	 * @return
	 */
	int setProductCategoryNull(long productCategoryId);
}
