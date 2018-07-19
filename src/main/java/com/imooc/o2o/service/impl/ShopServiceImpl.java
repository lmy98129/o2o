package com.imooc.o2o.service.impl;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.imooc.o2o.dao.ShopDao;
import com.imooc.o2o.dto.ImageHolder;
import com.imooc.o2o.dto.ShopExecution;
import com.imooc.o2o.entity.Shop;
import com.imooc.o2o.enums.ShopStateEnum;
import com.imooc.o2o.exceptions.ShopOperationException;
import com.imooc.o2o.service.ShopService;
import com.imooc.o2o.util.ImageUtil;
import com.imooc.o2o.util.PageCalculator;
import com.imooc.o2o.util.PathUtil;

@Service
public class ShopServiceImpl implements ShopService {
	@Autowired
	private ShopDao shopDao;
 
	@Override
	@Transactional
	public ShopExecution addShop(Shop shop, ImageHolder thumbnail) throws ShopOperationException {
		// 空值判断
		if (shop == null) {
			return new ShopExecution(ShopStateEnum.NULL_SHOP);
		}
		try {
			// 给店铺信息赋初始值
			shop.setEnableStatus(0);
			shop.setCreateTime(new Date());
			shop.setLastEditTime(new Date());
			// 添加店铺信息
			int affectedNum = shopDao.insertShop(shop);
			if (affectedNum <= 0) {
				throw new ShopOperationException("店铺创建失败");
			} else {
				if (thumbnail.getImage() != null) {
					// 存储图片
					try {
						addShopImg(shop, thumbnail);
					} catch (Exception e) {
						throw new ShopOperationException("addShopImg error:" + e.getMessage());
					}
					// 更新店铺的图片地址
					affectedNum = shopDao.updateShop(shop);
					if (affectedNum <= 0) {
						throw new ShopOperationException("更新图片地址失败");
					}
				}
			}
		} catch (Exception e) {
			throw new ShopOperationException("addShop error:" + e.getMessage());
		}
		return new ShopExecution(ShopStateEnum.CHECK, shop);
	}

	private void addShopImg(Shop shop, ImageHolder thumbnail) {
		// 获取shop图片目录的相对值路径
		String dest = PathUtil.getShopImagePath(shop.getShopId());
		String shopImgAddr = ImageUtil.generateThumbnail(thumbnail, dest);
		shop.setShopImg(shopImgAddr);
		// 在这里就已经拿到了这个地址
	}
	
	@Override
	public Shop getByShopId(long shopId) {
		Shop shop = shopDao.findShopById(shopId);
		return shop;
	}
	
	@Override
	public ShopExecution modifyShop(Shop shop, ImageHolder thumbnail)
			throws ShopOperationException {
		// 空值判断
		if (shop == null || shop.getShopId() == null) {
			return new ShopExecution(ShopStateEnum.NULL_SHOP);
		}
		// 判断是否需要添加图片
		if (thumbnail != null && thumbnail.getImageName() != null && thumbnail.getImage() != null &&
				!"".equals(thumbnail.getImageName())) {
			Shop tempShop = shopDao.findShopById(shop.getShopId());
			// 先将原有文件删除后再添加
			if (tempShop.getShopImg() != null) {
				ImageUtil.deleteFileOrPath(tempShop.getShopImg());
			}
			//再添加文件
			try {
				addShopImg(shop, thumbnail);
			} catch (Exception e) {
				throw new ShopOperationException("addShopImg error:" + e.getMessage());
			}
		}
		// 更新店铺信息
		shop.setLastEditTime(new Date());
		try {
			// 存入
			int affectedNum = shopDao.updateShop(shop);
			if (affectedNum <= 0) {
				throw new ShopOperationException("店铺编辑失败");
			}
		} catch (Exception e) {
			throw new ShopOperationException("modifyShop error:" + e.getMessage());
		}
		return new ShopExecution(ShopStateEnum.SUCCESS, shop);
	}
	
	@Override
	public ShopExecution getShopList(Shop shopCondition, int pageIndex,
			int pageSize) {
		int rowIndex = PageCalculator.calculateRowIndex(pageIndex, pageSize);
		List<Shop> shopList = shopDao.getShopList(shopCondition, rowIndex, pageSize);
		return new ShopExecution(ShopStateEnum.SUCCESS, shopList);
	}
}
