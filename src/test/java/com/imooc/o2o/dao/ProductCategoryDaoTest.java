package com.imooc.o2o.dao;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.imooc.o2o.BaseTest;
import com.imooc.o2o.entity.ProductCategory;

public class ProductCategoryDaoTest extends BaseTest{
	@Autowired
	private ProductCategoryDao productCategoryDao;
	
	@Test
	public void testQueryProductCategoryList() {
		List<ProductCategory> productCategoriyList= productCategoryDao.queryProductCategoryList(28L);
		System.out.println(productCategoriyList.size());
		System.out.println(productCategoriyList.get(0).getProductCategoryName());
	}
	
	@Test
	public void testBatchAddProductCategory() {
		ProductCategory productCategory1 = new ProductCategory();
		productCategory1.setShopId(67L);
		productCategory1.setCreateTime(new Date());
		productCategory1.setPriority(7);
		productCategory1.setProductCategoryName("内网安全");
		ProductCategory productCategory2 = new ProductCategory();
		productCategory2.setShopId(67L);
		productCategory2.setCreateTime(new Date());
		productCategory2.setPriority(7);
		productCategory2.setProductCategoryName("数据库安全");
		List<ProductCategory> productCategoryList = new ArrayList<ProductCategory>();
		productCategoryList.add(productCategory1);
		productCategoryList.add(productCategory2);
		int affectedNum = productCategoryDao.batchAddProductCategory(productCategoryList);
		System.out.println(affectedNum);
	}
	
	@Test
	public void testDeleteProductCategory() {
		int affectedNum = productCategoryDao.deleteProductCategory(15L, 67L);
		System.out.println(affectedNum);
	}
}
