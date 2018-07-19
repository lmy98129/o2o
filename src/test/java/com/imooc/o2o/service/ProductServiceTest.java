package com.imooc.o2o.service;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.imooc.o2o.BaseTest;
import com.imooc.o2o.dto.ImageHolder;
import com.imooc.o2o.dto.ProductExecution;
import com.imooc.o2o.entity.Product;
import com.imooc.o2o.entity.ProductCategory;
import com.imooc.o2o.entity.Shop;
import com.imooc.o2o.exceptions.ProductOperationException;

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
	
	@Test
	public void testGetProductById() {
		Product product = productService.getProductById(12L);
		System.out.println(product.getProductName());
	}
	
	@Test
	public void testAddProduct() throws FileNotFoundException, ProductOperationException {
		Product product = new Product();
		ProductCategory productCategory = new ProductCategory();
		Shop shop = new Shop();
		List<ImageHolder> imageHolderList = new ArrayList<ImageHolder>();
		product.setProductName("测试商品test");
		product.setProductDesc("测试中");
		product.setImgAddr("test");
		product.setNormalPrice("10");
		product.setPromotionPrice("9.5");
		product.setPriority(100);
		product.setCreateTime(new Date());
		product.setLastEditTime(new Date());
		product.setEnableStatus(1);
		productCategory.setProductCategoryId(12L);
		shop.setShopId(67L);
		product.setShop(shop);
		product.setProductCategory(productCategory);
		File productImg = new File("C:\\Users\\Blean\\a.jpg");
		InputStream is = new FileInputStream(productImg);
		ImageHolder imageHolder = new ImageHolder(productImg.getName(), is);
		File productImg1 = new File("C:\\Users\\Blean\\b.jpg");
		is = new FileInputStream(productImg1);
		ImageHolder tempImageHolder = new ImageHolder(productImg1.getName(), is);
		imageHolderList.add(tempImageHolder);
		File productImg2 = new File("C:\\Users\\Blean\\c.jpg");
		is = new FileInputStream(productImg2);
		tempImageHolder = new ImageHolder(productImg2.getName(), is);
		imageHolderList.add(tempImageHolder);
		ProductExecution pe = productService.addProduct(product, imageHolder, imageHolderList);
		System.out.println(pe.getStateInfo());
 	}
	
	@Test
	public void testModifyProduct() throws FileNotFoundException {
		Product product = new Product();
		ProductCategory productCategory = new ProductCategory();
		Shop shop = new Shop();
		List<ImageHolder> imageHolderList = new ArrayList<ImageHolder>();
		productCategory.setProductCategoryId(12L);
		shop.setShopId(67L);
		product.setShop(shop);
		product.setProductId(21L);
		product.setProductCategory(productCategory);
		product.setProductName("测试编辑商品");
		product.setPriority(100);
		File productImg = new File("C:\\Users\\Blean\\c.jpg");
		InputStream is = new FileInputStream(productImg);
		ImageHolder imageHolder = new ImageHolder(productImg.getName(), is);
		File productImg1 = new File("C:\\Users\\Blean\\d.jpg");
		is = new FileInputStream(productImg1);
		ImageHolder tempImageHolder = new ImageHolder(productImg1.getName(), is);
		imageHolderList.add(tempImageHolder);
		File productImg2 = new File("C:\\Users\\Blean\\e.jpg");
		is = new FileInputStream(productImg2);
		tempImageHolder = new ImageHolder(productImg2.getName(), is);
		imageHolderList.add(tempImageHolder);
		ProductExecution pe = productService.modifyProduct(product, imageHolder, imageHolderList);
		System.out.println(pe.getStateInfo());
	}
}
