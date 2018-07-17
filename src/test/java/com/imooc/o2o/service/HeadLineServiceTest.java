package com.imooc.o2o.service;

import static org.junit.Assert.assertEquals;

import java.util.List;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.imooc.o2o.BaseTest;
import com.imooc.o2o.entity.HeadLine;

public class HeadLineServiceTest extends BaseTest{
	@Autowired
	private HeadLineService headLineService;
	
	@Test
	public void testFindAllHeadLines() {
		List<HeadLine> headLineList = headLineService.findAllHeadLines();
		assertEquals("4", headLineList.get(0).getLineName());
	}
}
