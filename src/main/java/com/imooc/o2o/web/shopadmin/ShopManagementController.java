package com.imooc.o2o.web.shopadmin;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.multipart.commons.CommonsMultipartFile;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.imooc.o2o.dto.ImageHolder;
import com.imooc.o2o.dto.ShopExecution;
import com.imooc.o2o.entity.Area;
import com.imooc.o2o.entity.PersonInfo;
import com.imooc.o2o.entity.Shop;
import com.imooc.o2o.entity.ShopCategory;
import com.imooc.o2o.enums.ShopStateEnum;
import com.imooc.o2o.exceptions.ShopOperationException;
import com.imooc.o2o.service.AreaService;
import com.imooc.o2o.service.ShopCategoryService;
import com.imooc.o2o.service.ShopService;
import com.imooc.o2o.util.CodeUtil;
import com.imooc.o2o.util.HttpServletRequestUtil;

@Controller
@RequestMapping("/shopadmin")
public class ShopManagementController {
	@Autowired
	private ShopService shopService;

	@Autowired
	private AreaService areaService;
	
	@Autowired
	private ShopCategoryService shopCategoryService;

	@RequestMapping(value = "/registershop", method = RequestMethod.POST)
	@ResponseBody
	// 从表单中传来的店铺信息都会保存在request对象中
	private Map<String, Object> registerShop(HttpServletRequest request) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		//检查验证码
		if (!CodeUtil.checkVerifyCode(request)) {
			modelMap.put("success", false);
			modelMap.put("errMsg", "输入了错误的验证码");
			return modelMap;
		}
		// 1.接收并转化相应的参数，包括店铺信息以及图片信息，其实是在去除空串
		String shopStr = HttpServletRequestUtil.getString(request, "shopStr");
		// ObjectMapper是com.fasterxml.jackson.databind.ObjectMapper;
		// 用于json和pojo对象的转换。
		ObjectMapper mapper = new ObjectMapper();
		Shop shop = null;
		try {
			// 将店铺注册表单中接收到的shop信息转成shop对象，因为暂未得到图片路径，所以转化后shopImg为null
			shop = mapper.readValue(shopStr, Shop.class);
		} catch (Exception e) {
			modelMap.put("success", false);
			modelMap.put("errMsg", e.getMessage());
			return modelMap;
		}
		// 接收图片
		CommonsMultipartFile shopImg = null;
		// request.getSession().getServletContext() 从本次会话当中的上下文获取上传文件的内容
		CommonsMultipartResolver commonsMultipartResolver = new CommonsMultipartResolver(
				request.getSession().getServletContext());
		// 判断request中是否有上传的文件流
		if (commonsMultipartResolver.isMultipart(request)) {
			// 如果有，先做类型转换。将request转成multipartHttpServletRequest
			MultipartHttpServletRequest multipartHttpServletRequest = (MultipartHttpServletRequest) request;
			// 获取到文件流
			shopImg = (CommonsMultipartFile) multipartHttpServletRequest
					.getFile("shopImg");
		} else {
			modelMap.put("success", false);
			modelMap.put("errMsg", "上传图片不能为空");
			return modelMap;
		}
		// 2.注册店铺
		if (shop != null && shopImg != null) {
			/*//因为现在还没有做登录功能，无法在session中获取user信息，会导致在添加shop时，由于没有owner_id而报错
			PersonInfo owner = (PersonInfo) request.getSession().getAttribute("user");
			shop.setOwner(owner);*/
			//先将user写死
			PersonInfo owner = new PersonInfo();
			owner.setUserId(1L);
			shop.setOwner(owner);
			
			ShopExecution se;
			try {
				ImageHolder imageHolder = new ImageHolder(shopImg.getOriginalFilename(), shopImg.getInputStream());
				se = shopService.addShop(shop, imageHolder);
				if (se.getState() == ShopStateEnum.CHECK.getState()) {
					modelMap.put("success", true);
					// 该用户可以操作的店铺列表
					@SuppressWarnings("unchecked")
					List<Shop> shopList = (List<Shop>) request.getSession().getAttribute("shopList");
					if (shopList == null || shopList.size() == 0) {
						shopList = new ArrayList<Shop>();
					}
					shopList.add(se.getShop());
					request.getSession().setAttribute("shopList", shopList);
					// 添加到后端的session里面，可以省去一次SQL查询，如果有条件的话可以直接放到redis缓存里面
					// 而且不能将原有的进行删除，只能用原来的名称进行覆盖
				} else {
					modelMap.put("success", false);
					modelMap.put("errMsg", se.getStateInfo());
				}
			} catch (ShopOperationException e) {
				modelMap.put("success", false);
				modelMap.put("errMsg", e.getMessage());
			} catch (IOException e) {
				modelMap.put("success", false);
				modelMap.put("errMsg", e.getMessage());
			}
			return modelMap;
		} else {
			modelMap.put("success", false);
			modelMap.put("errMsg", "请输入店铺信息");
			return modelMap;
		}
	}

	@RequestMapping(value = "/getshopinitinfo", method = RequestMethod.GET)
	@ResponseBody
	private Map<String, Object> getShopInitInfo() {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		List<ShopCategory> shopCategoryList = new ArrayList<ShopCategory>();
		List<Area> areaList = new ArrayList<Area>();
		try {
			shopCategoryList = shopCategoryService
					.getShopCategoryList(new ShopCategory());
			areaList = areaService.getAreaList();
			modelMap.put("shopCategoryList", shopCategoryList);
			modelMap.put("areaList", areaList);
			modelMap.put("success", true);
		} catch (Exception e) {
			modelMap.put("success", false);
			modelMap.put("errMsg", e.getMessage());
		}
		return modelMap;
	}
	
	@RequestMapping(value = "/getshopbyid", method = RequestMethod.GET)
	@ResponseBody
	private Map<String, Object> getShopById(HttpServletRequest request) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		Shop shop = new Shop();
		List<Area> areaList = new ArrayList<Area>();
		long shopId = HttpServletRequestUtil.getLong(request, "shopId");
		try {
			shop = shopService.getByShopId(shopId);
			areaList = areaService.getAreaList();
			modelMap.put("success", true);
			modelMap.put("shop", shop);
			modelMap.put("areaList", areaList);
		} catch (Exception e) {
			modelMap.put("success", false);
			modelMap.put("errMsg", e.getMessage());
		}
		return modelMap;
	}
	
	@RequestMapping(value = "/modifyshop", method = RequestMethod.POST)
	@ResponseBody
	private Map<String, Object> modifyShop(HttpServletRequest request) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		//检查验证码
		if (!CodeUtil.checkVerifyCode(request)) {
			modelMap.put("success", false);
			modelMap.put("errMsg", "输入了错误的验证码");
			return modelMap;
		}
		// 1.接收并转化相应的参数，包括店铺信息以及图片信息，其实是在去除空串
		String shopStr = HttpServletRequestUtil.getString(request, "shopStr");
		// ObjectMapper是com.fasterxml.jackson.databind.ObjectMapper;
		// 用于json和pojo对象的转换。
		ObjectMapper mapper = new ObjectMapper();
		Shop shop = null;
		try {
			// 将店铺注册表单中接收到的shop信息转成shop对象，因为暂未得到图片路径，所以转化后shopImg为null
			shop = mapper.readValue(shopStr, Shop.class);
		} catch (Exception e) {
			modelMap.put("success", false);
			modelMap.put("errMsg", e.getMessage());
			return modelMap;
		}
		// 接收图片
		CommonsMultipartFile shopImg = null;
		// request.getSession().getServletContext() 从本次会话当中的上下文获取上传文件的内容
		CommonsMultipartResolver commonsMultipartResolver = new CommonsMultipartResolver(
				request.getSession().getServletContext());
		// 判断request中是否有上传的文件流
		if (commonsMultipartResolver.isMultipart(request)) {
			// 如果有，先做类型转换。将request转成multipartHttpServletRequest
			MultipartHttpServletRequest multipartHttpServletRequest = (MultipartHttpServletRequest) request;
			// 获取到文件流
			shopImg = (CommonsMultipartFile) multipartHttpServletRequest
					.getFile("shopImg");
		} else {
			modelMap.put("success", false);
			modelMap.put("errMsg", "上传图片不能为空");
			return modelMap;
		}
		if (shop != null && shopImg != null) {
			//先将user写死
			PersonInfo owner = new PersonInfo();
			owner.setUserId(1L);
			shop.setOwner(owner);
			
			ShopExecution se;
			try {
				ImageHolder imageHolder = new ImageHolder(shopImg.getOriginalFilename(), shopImg.getInputStream());
				se = shopService.modifyShop(shop, imageHolder);
				if (se.getState() == ShopStateEnum.CHECK.getState()) {
					modelMap.put("success", true);
					// 不用再加到session里面了
				} else {
					modelMap.put("success", false);
					modelMap.put("errMsg", se.getStateInfo());
				}
			} catch (ShopOperationException e) {
				modelMap.put("success", false);
				modelMap.put("errMsg", e.getMessage());
			} catch (IOException e) {
				modelMap.put("success", false);
				modelMap.put("errMsg", e.getMessage());
			}
			return modelMap;
		} else {
			modelMap.put("success", false);
			modelMap.put("errMsg", "请输入店铺信息");
			return modelMap;
		}
	}
	
	@RequestMapping(value = "/getshoplist", method = RequestMethod.GET)
	@ResponseBody
	private Map<String, Object> getShopList(HttpServletRequest request) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		ShopExecution shopExecution = new ShopExecution();
		Shop shopCondition = new Shop();
		PersonInfo user = new PersonInfo();
		user.setName("Blean");
		try {
			shopExecution = shopService.getShopList(shopCondition, 0, 100);
			if (shopExecution.getShopList().size() <= 0) {
				modelMap.put("success", false);
				modelMap.put("errMsg", "获取店铺列表失败");
				return modelMap;
			} else {
				modelMap.put("success", true);
				modelMap.put("shopList", shopExecution.getShopList());
				modelMap.put("user", user);
				request.getSession().setAttribute("shopList", shopExecution.getShopList());
				// 将取得的店铺列表放入session当中
				return modelMap;
			}
		} catch (Exception e) {
			modelMap.put("success", false);
			modelMap.put("errMsg", e.getMessage());
			return modelMap;
		}
	}
	
	@RequestMapping(value = "/getshopmanagementinfo", method = RequestMethod.GET)
	@ResponseBody
	private Map<String, Object> getShopManagementInfo(HttpServletRequest request) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		long shopId = HttpServletRequestUtil.getLong(request, "shopId");
		try {
			Shop shop = shopService.getByShopId(shopId);
			if (shop == null) {
				modelMap.put("redirect", true);
				modelMap.put("url", "/o2o/shopadmin/shoplist");
			} else {
				modelMap.put("redirect", false);
				modelMap.put("shopId", shopId);
			}
			return modelMap;
		} catch (Exception e) {
			modelMap.put("redirect", true);
			modelMap.put("url", "/o2o/shopadmin/shopList");
			return modelMap;
		}
	}
}
