package com.imooc.o2o.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.imooc.o2o.dao.ProductDao;
import com.imooc.o2o.dao.ProductImgDao;
import com.imooc.o2o.dto.ImageHolder;
import com.imooc.o2o.dto.ProductExecution;
import com.imooc.o2o.dto.ShopExecution;
import com.imooc.o2o.entity.Product;
import com.imooc.o2o.entity.ProductImg;
import com.imooc.o2o.enums.ProductStateEnum;
import com.imooc.o2o.enums.ShopStateEnum;
import com.imooc.o2o.exceptions.ProductOperationException;
import com.imooc.o2o.exceptions.ShopOperationException;
import com.imooc.o2o.service.ProductService;
import com.imooc.o2o.util.ImageUtil;
import com.imooc.o2o.util.PageCalculator;
import com.imooc.o2o.util.PathUtil;

@Service
public class ProductServiceImpl implements ProductService{
	
	@Autowired
	private ProductDao productDao;
	
	@Autowired
	private ProductImgDao productImgDao;

	@Override
	public ProductExecution getProductList(Product productCondition,
			int pageIndex, int pageSize) {
		int rowIndex = PageCalculator.calculateRowIndex(pageIndex, pageSize);
		List<Product> productList = productDao.getProductList(productCondition, rowIndex, pageSize);
		return new ProductExecution(ProductStateEnum.SUCCESS, productList);
	}

	@Override
	public Product getProductById(long productId) {
		return productDao.getProductById(productId);
	}

	@Override
	public ProductExecution addProduct(Product product, ImageHolder thumbnail,
			List<ImageHolder> productImgList) throws ProductOperationException {
		// 空值判断
		if (product == null) {
			return new ProductExecution(ProductStateEnum.EMPTY);
		} else {
			try {
				// 商品信息的初始值
				product.setEnableStatus(0);
				product.setCreateTime(new Date());
				product.setLastEditTime(new Date());
				// 添加商品信息
				int affectedNum = productDao.insertProduct(product);
				if (affectedNum <= 0) {
					throw new ProductOperationException("添加商品失败");
				} else {
					// 添加缩略图
					if (thumbnail.getImage() != null ) {
						try {
							addProductImg(product, thumbnail);
						} catch (Exception e) {
							throw new ProductOperationException("addProductImg error:" + e.getMessage());
						}
						affectedNum = productDao.updateProduct(product);
						if (affectedNum <= 0) {
							throw new ProductOperationException("更新缩略图地址失败");
						}
					}
					// 添加详情图
					List<ProductImg> productImgs;
					if (productImgList.size() != 0) {
						try {
							productImgs = batchAddProductImg(product, productImgList);
						} catch (Exception e) {
							throw new ProductOperationException("batchAddProductImg error:" + e.getMessage());
						}
						affectedNum = productImgDao.batchAddProductImg(productImgs);
						if (affectedNum <= 0) {
							throw new ProductOperationException("添加商品详情图失败");
						}
					}
				}
			} catch (Exception e) {
				throw new ProductOperationException("addProduct error:" + e.getMessage());
			}
		}
		return new ProductExecution(ProductStateEnum.SUCCESS, product);
	}

	@Override
	public ProductExecution modifyProduct(Product product,
			ImageHolder thumbnail, List<ImageHolder> productImgHolderList)
			throws ProductOperationException {
		if (product == null || product.getProductId() == null) {
			return new ProductExecution(ProductStateEnum.EMPTY);
		} else {
			// 判断是否需要添加缩略图
			if (thumbnail != null && thumbnail.getImageName() != null && thumbnail.getImage() != null &&
					!"".equals(thumbnail.getImageName())) {
				// 删除商品缩略图
				Product tempProduct = productDao.getProductById(product.getProductId());
				if(tempProduct.getImgAddr() != null) {
					ImageUtil.deleteFileOrPath(tempProduct.getImgAddr());
				}
				// 再添加文件
				try {
					addProductImg(product, thumbnail);
				} catch (Exception e) {
					throw new ProductOperationException("addProductImg error:" + e.getMessage());
				}
			}
			// 判断是否需要添加详情图
			if (productImgHolderList != null && productImgHolderList.size() != 0) {
				// 删除商品详情图
				List<ProductImg> tempProductImgList = productImgDao.getProductImgByProductId(product.getProductId());
				if (tempProductImgList != null && tempProductImgList.size() != 0) {
					for (ProductImg pi : tempProductImgList) {
						ImageUtil.deleteFileOrPath(pi.getImgAddr());
					}
					productImgDao.batchDeleteProductImg(product.getProductId());
				}
				// 再添加文件
				try {
					productImgDao.batchAddProductImg(batchAddProductImg(product, productImgHolderList));
				} catch (Exception e) {
					throw new ProductOperationException("batchAddProductImg error:" + e.getMessage());
				}
			}
			// 更新商品的其他信息
			product.setLastEditTime(new Date());
			try {
				int affctedNum = productDao.updateProduct(product);	
				if (affctedNum <= 0) {
					throw new ShopOperationException("店铺编辑失败");
				}
			} catch (Exception e) {
				throw new ProductOperationException("modifyProduct error:" + e.getMessage());
			}
		}
		return new ProductExecution(ProductStateEnum.SUCCESS, product);
	}
	
	private void addProductImg(Product product, ImageHolder thumbnail) {
		String dest = PathUtil.getShopImagePath(product.getShop().getShopId());
		String productImgAddr = ImageUtil.generateThumbnail(thumbnail, dest);
		product.setImgAddr(productImgAddr);
	}
	
	private List<ProductImg> batchAddProductImg(Product product, List<ImageHolder> imageHolderList) {
		String productImgAddr;
		List<ProductImg> productImgList = new ArrayList<ProductImg>();
		String dest = PathUtil.getShopImagePath(product.getShop().getShopId());
		for(ImageHolder ih : imageHolderList) {
			ProductImg tempImg = new ProductImg();
			productImgAddr = ImageUtil.generateNormalImg(ih, dest);
			tempImg.setImgAddr(productImgAddr);
			tempImg.setCreateTime(new Date());
			tempImg.setProductId(product.getProductId());
			productImgList.add(tempImg);
		}
		return productImgList;
	}
	
}
