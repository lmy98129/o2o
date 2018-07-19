package com.imooc.o2o.dao;

import java.util.Date;
import java.util.List;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.imooc.o2o.BaseTest;
import com.imooc.o2o.entity.Product;
import com.imooc.o2o.entity.ProductCategory;
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
	
	@Test
	public void testGetProductById() {
		Product product = new Product();
		product.setProductId(12L);
		product = productDao.getProductById(product.getProductId());
		System.out.println(product.getProductName());
	}
	
	@Test
	public void testUpdateProduct() {
		Product product = new Product();
		product = productDao.getProductById(1L);
		product.setEnableStatus(0);
		int affectedNum = productDao.updateProduct(product);
		System.out.println(affectedNum);
	}
	
	@Test
	public void testInsertProduct() {
		Product product = new Product();
		Shop shop = new Shop();
		ProductCategory productCategory = new ProductCategory();
		shop.setShopId(67L);
		productCategory.setProductCategoryId(12L);
		product.setProductName("testing");
		product.setProductDesc("testing");
		product.setImgAddr("testing");
		product.setNormalPrice("11");
		product.setPromotionPrice("9.5");
		product.setPriority(7);
		product.setCreateTime(new Date());
		product.setLastEditTime(new Date());
		product.setEnableStatus(1);
		product.setShop(shop);
		product.setProductCategory(productCategory);
		int affectedNum = productDao.insertProduct(product);
		System.out.println(affectedNum);
	}
}
