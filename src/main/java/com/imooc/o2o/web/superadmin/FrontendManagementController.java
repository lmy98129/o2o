package com.imooc.o2o.web.superadmin;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.imooc.o2o.entity.HeadLine;
import com.imooc.o2o.entity.ShopCategory;
import com.imooc.o2o.service.HeadLineService;
import com.imooc.o2o.service.ShopCategoryService;

@Controller
@RequestMapping("/frontend")
public class FrontendManagementController {
	Logger logger = LoggerFactory.getLogger(FrontendManagementController.class);
	@Autowired
	private HeadLineService headLineService;
	@Autowired
	private ShopCategoryService shopCategoryService;

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
	
}