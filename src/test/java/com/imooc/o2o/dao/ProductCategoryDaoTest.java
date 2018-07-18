package com.imooc.o2o.dao;

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
}
