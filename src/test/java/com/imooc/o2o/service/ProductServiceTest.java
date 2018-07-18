package com.imooc.o2o.service;

import java.util.List;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.imooc.o2o.BaseTest;
import com.imooc.o2o.dto.ProductExecution;
import com.imooc.o2o.entity.Product;
import com.imooc.o2o.entity.Shop;

public class ProductServiceTest extends BaseTest{
	@Autowired
	private ProductService productService;
	
	@Test
	public void testGetProductList() {
		Product productCondition = new Product();
		ProductExecution pe = new ProductExecution();
		Shop shop = new Shop();
		shop.setShopId(28L);
		productCondition.setShop(shop);
		pe = productService.getProductList(productCondition, 1, 999);
		System.out.println(pe.getProductList().size());
		System.out.println(pe.getProductList().get(0).getProductName());
	}
}
