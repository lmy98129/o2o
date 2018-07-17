package com.imooc.o2o.service;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.imooc.o2o.BaseTest;
import com.imooc.o2o.entity.ShopCategory;

public class ShopCategoryServiceTest extends BaseTest{
	@Autowired
	private ShopCategoryService shopCategoryService;
	
	@Test
	public void testFindTopShopCategories() {
		List<ShopCategory> shopCategoryList = new ArrayList<ShopCategory>();
		shopCategoryList = shopCategoryService.getShopCategoryList(null);
		System.out.println(shopCategoryList.get(0).toString());
		shopCategoryList = shopCategoryService.getShopCategoryList(new ShopCategory());
		System.out.println(shopCategoryList.get(0).toString());
		ShopCategory shopCategory = new ShopCategory();
		ShopCategory parent = new ShopCategory();
		parent.setShopCategoryId((long) 12);
		shopCategory.setParent(parent);
		shopCategoryList = shopCategoryService.getShopCategoryList(shopCategory);
		System.out.println(shopCategoryList.get(0).toString());
	}
}
