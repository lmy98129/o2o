package com.imooc.o2o.dao;

import java.util.List;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.imooc.o2o.BaseTest;
import com.imooc.o2o.entity.Product;
import com.imooc.o2o.entity.Shop;

public class ProductDaoTest extends BaseTest{
	@Autowired
	private ProductDao productDao;
	
	@Test
	public void testSetProductCategoryNull() {
		int affectedNum = productDao.setProductCategoryNull(3L);
		System.out.println(affectedNum);
	}
	
	@Test
	public void testGetProductList() {
		Product productCondition = new Product();
		Shop shop = new Shop();
		shop.setShopId(28L);
		productCondition.setShop(shop);
		List<Product> productList = productDao.getProductList(productCondition, 0, 999);
		System.out.println(productList.size());
		System.out.println(productList.get(0).getProductName());
	}
}
