package com.imooc.o2o.dao;

import static org.junit.Assert.assertEquals;

import java.util.Date;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.imooc.o2o.BaseTest;
import com.imooc.o2o.entity.Area;
import com.imooc.o2o.entity.PersonInfo;
import com.imooc.o2o.entity.Shop;
import com.imooc.o2o.entity.ShopCategory;

public class ShopDaoTest extends BaseTest {
	@Autowired
	private ShopDao shopDao;
	
	@Test
	public void testInsertShop() {
		Shop shop = new Shop();
		System.out.println("插入前主键为："+shop.getShopId());
		PersonInfo owner = new PersonInfo();
		Area area = new Area();
		ShopCategory shopCategory = new ShopCategory();
		// shop需要owner、area以及shopCategory传入的值
		owner.setUserId(1L);
		area.setAreaId(2);
		shopCategory.setShopCategoryId(10L);
		shop.setOwner(owner);
		shop.setArea(area);
		shop.setShopCategory(shopCategory);
		shop.setShopName("测试中");
		shop.setShopDesc("testing desc");
		shop.setShopAddr("testing addr");
		shop.setPhone("123");
		shop.setShopImg("nothing");
		shop.setCreateTime(new Date());
		shop.setEnableStatus(0);
		shop.setAdvice("审核中");
		int effectedNum = shopDao.insertShop(shop);
		System.out.println("插入后主键为："+shop.getShopId());
		// 这就相当于传入的对象事实上也与数据库的内容进行同步更改了
		assertEquals(1, effectedNum);
	}
	
	@Test
	public void testUpdateShop() {
		Shop shop = new Shop();
		shop.setShopId(1L);
		shop.setShopDesc("testing desc");
		shop.setShopAddr("testing addr");
		shop.setLastEditTime(new Date());
		int effectedNum = shopDao.updateShop(shop);
		assertEquals(1, effectedNum);
	}
	
	@Test
	public void testFindShopById() {
		Shop shop = shopDao.findShopById(28L);
		System.out.println(shop.getShopName());
		System.out.println(shop.getArea().getAreaName());
	}
}
