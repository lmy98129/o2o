package com.imooc.o2o.dao;


import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.imooc.o2o.BaseTest;
import com.imooc.o2o.entity.ShopCategory;

public class ShopCategoryDaoTest extends BaseTest {
	@Autowired
	private ShopCategoryDao shopCategoryDao;
	
	@Test
	public void testFindTopShopCategories() {
		ShopCategory shopCategory = new ShopCategory();
		ShopCategory parent = new ShopCategory();
		List<ShopCategory> shopCategoryList = new ArrayList<ShopCategory>();
		shopCategoryList = shopCategoryDao.queryShopCategory(null);
		System.out.println(shopCategoryList.size());
		shopCategoryList = shopCategoryDao.queryShopCategory(shopCategory);
		System.out.println(shopCategoryList.size());
		parent.setShopCategoryId((long) 12);
		shopCategory.setParent(parent);
		shopCategoryList = shopCategoryDao.queryShopCategory(shopCategory);
		System.out.println(shopCategoryList.size());
		
	}
}
