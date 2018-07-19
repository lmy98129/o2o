package com.imooc.o2o.dao;

import java.util.List;

import com.imooc.o2o.dto.ImageHolder;
import com.imooc.o2o.entity.ProductImg;

public interface ProductImgDao {
	
	/**
	 * 批量添加商品图片
	 * 
	 * @param imgeList
	 * @return
	 */
	int batchAddProductImg(List<ProductImg> productImgList);
	
	/**
	 * 批量删除商品图片
	 * 
	 * @param productId
	 * @return
	 */
	int batchDeleteProductImg(long productId);
	
	/**
	 * 批量获取商品详情图片
	 * 
	 * @param productId
	 * @return
	 */
	List<ProductImg> getProductImgByProductId(long productId);
}
