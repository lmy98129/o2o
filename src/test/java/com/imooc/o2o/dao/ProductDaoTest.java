package com.imooc.o2o.dao;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.imooc.o2o.BaseTest;

public class ProductDaoTest extends BaseTest{
	@Autowired
	private ProductDao productDao;
	
	@Test
	public void testSetProductCategoryNull() {
		int affectedNum = productDao.setProductCategoryNull(3L);
		System.out.println(affectedNum);
	}
}
