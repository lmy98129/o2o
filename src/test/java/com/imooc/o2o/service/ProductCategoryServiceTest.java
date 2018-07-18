package com.imooc.o2o.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.imooc.o2o.BaseTest;
import com.imooc.o2o.dto.ProductCategoryExecution;
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
		ProductCategoryExecution pe = productCategoryService.batchAddProductCategory(productCategoryList);
		System.out.println(pe.getStateInfo());
	}
	
	@Test
	public void testDeleteProductCategory() {
		ProductCategoryExecution pce = productCategoryService.deleteProductCategory(15L, 67L);
		System.out.println(pce.getStateInfo());
	}
}
