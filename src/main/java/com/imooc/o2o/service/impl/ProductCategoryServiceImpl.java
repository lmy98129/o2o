package com.imooc.o2o.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.imooc.o2o.dao.ProductCategoryDao;
import com.imooc.o2o.dao.ProductDao;
import com.imooc.o2o.dto.ProductCategoryExecution;
import com.imooc.o2o.entity.ProductCategory;
import com.imooc.o2o.enums.ProductCategoryStateEnum;
import com.imooc.o2o.exceptions.ProductCategoryOperationException;
import com.imooc.o2o.service.ProductCategoryService;

@Service
public class ProductCategoryServiceImpl implements ProductCategoryService{
	
	@Autowired
	private ProductCategoryDao productCategoryDao;
	
	@Autowired
	private ProductDao productDao;
	
	@Override
	public List<ProductCategory> getProductCategoryList(long shopId) {
		return productCategoryDao.queryProductCategoryList(shopId);
	}

	@Override
	public ProductCategoryExecution batchAddProductCategory(
			List<ProductCategory> productCategoryList)
			throws ProductCategoryOperationException {
		if(productCategoryList == null || productCategoryList.size() == 0) {
			return new ProductCategoryExecution(ProductCategoryStateEnum.EMPTY_LIST);
		} else {
			try {
				int affectedNum = productCategoryDao.batchAddProductCategory(productCategoryList);
				if (affectedNum <= 0) {
					throw new ProductCategoryOperationException("批量插入商品类别失败");
				} else {
					return new ProductCategoryExecution(ProductCategoryStateEnum.SUCCESS);			
				}
			} catch (Exception e) {
				throw new ProductCategoryOperationException("addProductCategory error: " + e.getMessage());
			}
		}
	}

	@Override
	public ProductCategoryExecution deleteProductCategory(
			long productCategoryId, long shopId)
			throws ProductCategoryOperationException {
		// 将商品与商品类别的关系进行解除
		try {
			int affectedNum = productDao.setProductCategoryNull(productCategoryId);
			if (affectedNum < 0) {
				throw new ProductCategoryOperationException("商品与商品类别的关系解除失败");
			}
		} catch (Exception e) {
			throw new ProductCategoryOperationException("deleteProductCategory error: "+e.getMessage());
		}
		// 删除商品类别
		try {
			int affectedNum = productCategoryDao.deleteProductCategory(productCategoryId, shopId);
			if (affectedNum <= 0) {
				throw new ProductCategoryOperationException("删除商品类别失败");
			} else {
				return new ProductCategoryExecution(ProductCategoryStateEnum.SUCCESS);
			}
		} catch (Exception e) {
			throw new ProductCategoryOperationException("deleteProductCategory error: "+e.getMessage());
		}
	}
}
