package com.imooc.o2o.service;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Date;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.imooc.o2o.BaseTest;
import com.imooc.o2o.dto.ImageHolder;
import com.imooc.o2o.dto.ShopExecution;
import com.imooc.o2o.entity.Area;
import com.imooc.o2o.entity.PersonInfo;
import com.imooc.o2o.entity.Shop;
import com.imooc.o2o.entity.ShopCategory;
import com.imooc.o2o.enums.ShopStateEnum;
import com.imooc.o2o.exceptions.ShopOperationException;

public class ShopServiceTest extends BaseTest{
	@Autowired
	private ShopService shopService;
	
	@Test
	public void testAddShop() throws ShopOperationException, FileNotFoundException{
		Shop shop = new Shop();
		PersonInfo owner = new PersonInfo();
		Area area = new Area();
		ShopCategory shopCategory = new ShopCategory();
		//初始化
		owner.setUserId(1L);
		area.setAreaId(2);
		shopCategory.setShopCategoryId(10L);
		shop.setOwner(owner);
		shop.setArea(area);
		shop.setShopCategory(shopCategory);
		shop.setShopName("Endless Testing");
		shop.setShopDesc("Testing Testing");
		shop.setShopAddr("Testing Here");
		shop.setPhone("none");
		shop.setCreateTime(new Date());
		shop.setEnableStatus(ShopStateEnum.CHECK.getState());
		shop.setAdvice("审核中");
		File shopImg = new File("C:\\Users\\Blean\\a.jpg");
		InputStream is = new FileInputStream(shopImg);
		ImageHolder imageHolder = new ImageHolder(shopImg.getName(), is);
		ShopExecution se = shopService.addShop(shop, imageHolder);
		assertEquals(ShopStateEnum.CHECK.getState(), se.getState());
	}
	
	@Test
	public void testGetShopById() {
		Shop shop = shopService.getByShopId(28L);
		System.out.println(shop.getShopName());
		System.out.println(shop.getArea().getAreaName());
	}
	
	@Test
	public void testModifyShop() throws FileNotFoundException {
		Shop shop = new Shop();
		Area area = new Area();
		ShopCategory shopCategory = new ShopCategory();
		PersonInfo owner = new PersonInfo();
		owner.setUserId(1L);
		area.setAreaId(2);
		shopCategory.setShopCategoryId(10L);
		shop.setShopId(67L);
		shop.setOwner(owner);
		shop.setArea(area);
		shop.setShopCategory(shopCategory);
		shop.setShopName("Endless Testing");
		shop.setShopDesc("Testing Testing");
		shop.setShopAddr("Testing Here");
		shop.setPhone("none");
		File shopImg = new File("C:\\Users\\Blean\\a.jpg");
		InputStream is = new FileInputStream(shopImg);
		ImageHolder imageHolder = new ImageHolder(shopImg.getName(), is);
		ShopExecution se = shopService.modifyShop(shop, imageHolder);
	}
	
	@Test
	public void testGetShopList() {
		Shop shopCondition = new Shop();
		ShopCategory shopCategory = new ShopCategory();
		ShopCategory parent = new ShopCategory();
		shopCategory.setShopCategoryId(22L);
		shopCondition.setShopCategory(shopCategory);
		ShopExecution se = shopService.getShopList(shopCondition, 0, 3);
		System.out.println(se.getShopList().get(0).getShopName());
		shopCategory.setShopCategoryDesc(null);
		parent.setShopCategoryId(12L);
		shopCategory.setParent(parent);
		se = shopService.getShopList(shopCondition, 1, 3);
		System.out.println(se.getShopList().get(0).getShopName());
	}
}
