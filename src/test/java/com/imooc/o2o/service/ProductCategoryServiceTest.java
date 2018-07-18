package com.imooc.o2o.service;

import java.util.List;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.imooc.o2o.BaseTest;
import com.imooc.o2o.entity.ProductCategory;

public class ProductCategoryServiceTest extends BaseTest{
	@Autowired
	private ProductCategoryService productCategoryService;
	
	@Test
	public void testGetProductCategoryList() {
		List<ProductCategory> productCategoryList = productCategoryService.getProductCategoryList(28L);
		System.out.println(productCategoryList.size());
		System.out.println(productCategoryList.get(0).getProductCategoryName());
	}
}
