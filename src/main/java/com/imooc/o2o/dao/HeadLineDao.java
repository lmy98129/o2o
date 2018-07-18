package com.imooc.o2o.dao;

import java.util.List;

import com.imooc.o2o.entity.HeadLine;

public interface HeadLineDao {
	/**
	 * 列出所有的头条
	 * 
	 * @return headLineList
	 */
	List<HeadLine> findAllHeadLines();
}
