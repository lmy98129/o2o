package com.imooc.o2o.dao;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.imooc.o2o.BaseTest;
import com.imooc.o2o.entity.ProductImg;

public class ProductImgDaoTest extends BaseTest{
	
	@Autowired
	private ProductImgDao productImgDao;
	
	@Test
	public void testBatchAddProductImg() {
		List<ProductImg> productImgList = new ArrayList<ProductImg>();
		ProductImg productImg1 = new ProductImg();
		ProductImg productImg2 = new ProductImg();
		productImg1.setImgAddr("test1");
		productImg1.setImgDesc("test1");
		productImg1.setPriority(100);
		productImg1.setCreateTime(new Date());
		productImg1.setProductId(15L);
		productImg2.setImgAddr("test2");
		productImg2.setImgDesc("test2");
		productImg2.setPriority(100);
		productImg2.setCreateTime(new Date());
		productImg2.setProductId(15L);
		productImgList.add(productImg1);
		productImgList.add(productImg2);
		int affecedNum = productImgDao.batchAddProductImg(productImgList);
		System.out.println(affecedNum);
	}
	
	@Test
	public void testBatchDeleteProductImg() {
		int affectedNum = productImgDao.batchDeleteProductImg(15L);
		System.out.println(affectedNum);
	}
	
	@Test
	public void testGetProductImgByProductId() {
		List<ProductImg> productImgList = productImgDao.getProductImgByProductId(17L);
		System.out.println(productImgList.size());
		System.out.println(productImgList.get(0).getImgAddr());
	}
	
}
