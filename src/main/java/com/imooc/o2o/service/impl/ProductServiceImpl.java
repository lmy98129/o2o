package com.imooc.o2o.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.imooc.o2o.dao.ProductDao;
import com.imooc.o2o.dto.ImageHolder;
import com.imooc.o2o.dto.ProductExecution;
import com.imooc.o2o.entity.Product;
import com.imooc.o2o.enums.ProductStateEnum;
import com.imooc.o2o.exceptions.ProductOperationException;
import com.imooc.o2o.service.ProductService;
import com.imooc.o2o.util.PageCalculator;

@Service
public class ProductServiceImpl implements ProductService{
	
	@Autowired
	private ProductDao productDao;

	@Override
	public ProductExecution getProductList(Product productCondition,
			int pageIndex, int pageSize) {
		int rowIndex = PageCalculator.calculateRowIndex(pageIndex, pageSize);
		List<Product> productList = productDao.getProductList(productCondition, rowIndex, pageSize);
		return new ProductExecution(ProductStateEnum.SUCCESS, productList);
	}

	@Override
	public Product getProductById(long productId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ProductExecution addProduct(Product product, ImageHolder thumbnail,
			List<ImageHolder> productImgList) throws ProductOperationException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ProductExecution modifyProduct(Product product,
			ImageHolder thumbnail, List<ImageHolder> productImgHolderList)
			throws ProductOperationException {
		// TODO Auto-generated method stub
		return null;
	}
	
}
