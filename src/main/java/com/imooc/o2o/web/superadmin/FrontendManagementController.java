package com.imooc.o2o.web.superadmin;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.imooc.o2o.dto.ProductExecution;
import com.imooc.o2o.dto.ShopExecution;
import com.imooc.o2o.entity.Area;
import com.imooc.o2o.entity.HeadLine;
import com.imooc.o2o.entity.Product;
import com.imooc.o2o.entity.ProductCategory;
import com.imooc.o2o.entity.Shop;
import com.imooc.o2o.entity.ShopCategory;
import com.imooc.o2o.service.AreaService;
import com.imooc.o2o.service.HeadLineService;
import com.imooc.o2o.service.ProductCategoryService;
import com.imooc.o2o.service.ProductService;
import com.imooc.o2o.service.ShopCategoryService;
import com.imooc.o2o.service.ShopService;

@Controller
@RequestMapping("/frontend")
public class FrontendManagementController {
	Logger logger = LoggerFactory.getLogger(FrontendManagementController.class);
	@Autowired
	private HeadLineService headLineService;
	
	@Autowired
	private ShopCategoryService shopCategoryService;
	
	@Autowired
	private ShopService shopService;
	
	@Autowired
	private AreaService areaService;
	
	@Autowired
	private ProductCategoryService productCategoryService;
	
	@Autowired
	private ProductService productService;

	@RequestMapping(value = "/listmainpageinfo",  method = RequestMethod.GET)
	@ResponseBody
	private Map<String, Object> listMainPageInfo() {
		logger.info("===start===");
		long startTime = System.currentTimeMillis();
		Map<String, Object> modelMap = new HashMap<String, Object>();
		List<HeadLine> headLineList = new ArrayList<HeadLine>();
		List<ShopCategory> shopCategoryList = new ArrayList<ShopCategory>();
		try{
			headLineList = headLineService.findAllHeadLines();
			shopCategoryList = shopCategoryService.getShopCategoryList(null);
			modelMap.put("success", true);
			modelMap.put("headLineList", headLineList);
			modelMap.put("shopCategoryList", shopCategoryList);
		} catch (Exception e) {
			e.printStackTrace();
			modelMap.put("success", false);
			modelMap.put("errMsg", e.toString());
			logger.error("error at index",e);
		}
		long endTime = System.currentTimeMillis();
		logger.debug("costTime:[{}ms]", endTime - startTime);
		logger.info("===end===");
		return modelMap;
	}
	
	@RequestMapping(value = "/listshopspageinfo",  method = RequestMethod.GET)
	@ResponseBody
	private Map<String, Object> listShopsPageInfo(Long parentId, HttpServletRequest request) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		ShopCategory parent = new ShopCategory();
		ShopCategory shopCategoryCondition = new ShopCategory();
		parent.setShopCategoryId(parentId);
		shopCategoryCondition.setParent(parent);
		List<ShopCategory> shopCategoryList = new ArrayList<ShopCategory>();
		try {
			shopCategoryList = shopCategoryService.getShopCategoryList(shopCategoryCondition);
			if(shopCategoryList.size() <= 0) {
				modelMap.put("success", false);
				modelMap.put("errMsg", "获取店铺分类列表失败");
				return modelMap;
			} else {
				modelMap.put("shopCategoryList", shopCategoryList);
			}
		} catch (Exception e) {
			modelMap.put("success", false);
			modelMap.put("errMsg", e.getMessage());
			return modelMap;
		}
		
		List<Area> areaList = new ArrayList<Area>();
		try {
			areaList = areaService.getAreaList();
			if (areaList.size() <= 0) {
				modelMap.put("success", false);
				modelMap.put("errMsg", "获取地区列表失败");
				return modelMap;
			} else {
				modelMap.put("success", true);
				modelMap.put("areaList", areaList);
				return modelMap;
			}
		} catch (Exception e) {
			modelMap.put("success", false);
			modelMap.put("errMsg", e.getMessage());
			return modelMap;
		}
	}
	
	@RequestMapping(value = "/listshops", method = RequestMethod.GET)
	@ResponseBody
	private Map<String, Object> listShops(
			Integer pageIndex, Integer pageSize, Long parentId, 
			Integer areaId, Long shopCategoryId, String shopName, HttpServletRequest request) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		Shop shopCondition = new Shop();
		ShopCategory shopCategory = new ShopCategory();
		Area area = new Area();
		ShopCategory parent = new ShopCategory();
		if (parentId != null) {
			parent.setShopCategoryId(parentId);
			shopCategory.setParent(parent);
			shopCondition.setShopCategory(shopCategory);
		}
		if (areaId != null) {
			area.setAreaId(areaId);
			shopCondition.setArea(area);
		}
		if (shopCategoryId != null) {
			shopCategory.setShopCategoryId(shopCategoryId);
			shopCondition.setShopCategory(shopCategory);
		}
		if (shopName != null && !"".equals(shopName)) {
			shopCondition.setShopName(shopName);
		}
		ShopExecution se;
		try {
			se = shopService.getShopList(shopCondition, pageIndex, pageSize);
			if (se.getState() != 1) {
				modelMap.put("success", false);
				modelMap.put("errMsg", se.getStateInfo());
				return modelMap;
			} else {
				modelMap.put("success", true);
				modelMap.put("shopList", se.getShopList());
				return modelMap;
			}
		} catch (Exception e) {
			modelMap.put("success", false);
			modelMap.put("errMsg", e.getMessage());
			return modelMap;
		}
	}
	
	@RequestMapping(value = "/listshopdetailpageinfo",  method = RequestMethod.GET)
	@ResponseBody
	Map<String, Object> listShopDetailPageInfo(Long shopId, HttpServletRequest request) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		Shop shop = new Shop();
		List<ProductCategory> productCategoryList = new ArrayList<ProductCategory>();
		try {
			shop = shopService.getByShopId(shopId);
			if(shop == null) {
				modelMap.put("success", false);
				modelMap.put("errMsg", "店铺无效");
				return modelMap;
			} else {
				modelMap.put("shop", shop);
			}
		} catch (Exception e) {
			modelMap.put("success", false);
			modelMap.put("errMsg", e.getMessage());
			return modelMap;
		}
		try {
			productCategoryList = productCategoryService.getProductCategoryList(shopId);
			if (productCategoryList.size() <= 0) {
				modelMap.put("errMsg", "获取商品类别列表失败");
			} else {
				modelMap.put("productCategoryList", productCategoryList);
			}
			modelMap.put("success", true);
			return modelMap;
		} catch (Exception e) {
			modelMap.put("success", false);
			modelMap.put("errMsg", e.getMessage());
			return modelMap;
		}
	}
	
	@RequestMapping(value = "/listproductsbyshop",  method = RequestMethod.GET)
	@ResponseBody
	Map<String, Object> listProductsByShop(Integer pageIndex, Integer pageSize, Long productCategoryId, 
			String productName, Long shopId, HttpServletRequest request) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		Product productCondition = new Product();
		ProductCategory productCategory = new ProductCategory();
		Shop shop = new Shop();
		if (productCategoryId != null) {
			productCategory.setProductCategoryId(productCategoryId);
			productCondition.setProductCategory(productCategory);
		}
		if (productName != null && !"".equals(productName)) {
			productCondition.setProductName(productName);
		}
		if (shopId != null) {
			shop.setShopId(shopId);
			productCondition.setShop(shop);
		}
		ProductExecution pe;
		try {
			pe = productService.getProductList(productCondition, pageIndex, pageSize);
			if (pe.getState() != 1) {
				modelMap.put("success", false);
				modelMap.put("errMsg", pe.getStateInfo());
				return modelMap;
			} else {
				modelMap.put("success", true);
				modelMap.put("productList", pe.getProductList());
				return modelMap;
			}
		} catch (Exception e) {
			modelMap.put("success", false);
			modelMap.put("errMsg", e.getMessage());
			return modelMap;
		}
	}
	
	@RequestMapping(value = "/listproductdetailpageinfo",  method = RequestMethod.GET)
	@ResponseBody
	Map<String, Object> listProductDetialPageInfo(Long productId, HttpServletRequest request) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		Product product = new Product();
		try {
			product = productService.getProductById(productId);
			if (product == null) {
				modelMap.put("success", false);
				modelMap.put("errMsg", "商品不存在");
				return modelMap;
			} else {
				modelMap.put("success", true);
				modelMap.put("product", product);
				return modelMap;
			}
		} catch (Exception e) {
			modelMap.put("success", false);
			modelMap.put("errMsg", e.getMessage());
			return modelMap;
		}
	}
}