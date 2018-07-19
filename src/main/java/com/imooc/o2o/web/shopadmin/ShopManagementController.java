package com.imooc.o2o.web.shopadmin;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.multipart.commons.CommonsMultipartFile;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.imooc.o2o.dto.ImageHolder;
import com.imooc.o2o.dto.ProductCategoryExecution;
import com.imooc.o2o.dto.ProductExecution;
import com.imooc.o2o.dto.ShopExecution;
import com.imooc.o2o.entity.Area;
import com.imooc.o2o.entity.PersonInfo;
import com.imooc.o2o.entity.Product;
import com.imooc.o2o.entity.ProductCategory;
import com.imooc.o2o.entity.Shop;
import com.imooc.o2o.entity.ShopCategory;
import com.imooc.o2o.enums.ShopStateEnum;
import com.imooc.o2o.exceptions.ShopOperationException;
import com.imooc.o2o.service.AreaService;
import com.imooc.o2o.service.ProductCategoryService;
import com.imooc.o2o.service.ProductService;
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
	
	@Autowired
	private ProductCategoryService productCategoryService;
	
	@Autowired
	private ProductService productService;

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
		// 接收并转化相应的参数，包括店铺信息以及图片信息，其实是在去除空串
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
		}
		if (shop != null && shop.getShopId() != null) {			
			ShopExecution se;
			try {
				if (shopImg == null) {
					se = shopService.modifyShop(shop, null);
				} else {
					ImageHolder imageHolder = new ImageHolder(shopImg.getOriginalFilename(), shopImg.getInputStream());
					se = shopService.modifyShop(shop, imageHolder);					
				}
				if (se.getState() == ShopStateEnum.SUCCESS.getState()) {
					modelMap.put("success", true);
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
			@SuppressWarnings("unchecked")
			List<Shop> shopList = (List<Shop>) request.getSession().getAttribute("shopList");
			Shop currentShop = new Shop();
			boolean isShopNotExist = true;
			for(int i=0; i<shopList.size() ; i++) {
				if (shopList.get(i).getShopId() == shopId) {
					isShopNotExist = false;
					currentShop = shopList.get(i);
				}
			}
			if (isShopNotExist) {
				modelMap.put("redirect", true);
				modelMap.put("url", "/o2o/shopadmin/shoplist");
			} else {
				modelMap.put("redirect", false);
				modelMap.put("shopId", shopId);
				request.getSession().setAttribute("currentShop", currentShop);
			}
			return modelMap;
		} catch (Exception e) {
			modelMap.put("redirect", true);
			modelMap.put("url", "/o2o/shopadmin/shoplist");
			return modelMap;
		}
	}
	
	@RequestMapping(value = "/getproductcategorylist", method = RequestMethod.GET)
	@ResponseBody
	private Map<String, Object> getProductCategoryList(HttpServletRequest request) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		Shop currentShop = (Shop) request.getSession().getAttribute("currentShop");
		List<ProductCategory> productCategoryList = productCategoryService.getProductCategoryList(currentShop.getShopId());
		try {
			if (productCategoryList.size() <= 0) {
				modelMap.put("success", false);
				modelMap.put("errMsg", "获取商品类别列表失败");
				return modelMap;
			} else {
				modelMap.put("success", true);
				modelMap.put("data", productCategoryList);
				return modelMap;
			}
		} catch (Exception e) {
			modelMap.put("success", false);
			modelMap.put("errMsg", e.getMessage());
			return modelMap;
		}
	}
	
	@RequestMapping(value = "/addproductcategorys", method = RequestMethod.POST)
	@ResponseBody
	private Map<String, Object> addProductCategories(@RequestBody List<ProductCategory> productCategoryList,
			HttpServletRequest request) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		Shop currentShop = (Shop) request.getSession().getAttribute("currentShop");
		if (productCategoryList == null || productCategoryList.size() <= 0) {
			modelMap.put("success", false);
			modelMap.put("errMsg", "提交的商品类别列表为空");
			return modelMap;
		} else {
			for(ProductCategory productCategory : productCategoryList) {
				productCategory.setShopId(currentShop.getShopId());
				productCategory.setCreateTime(new Date());
			}
			try {
				ProductCategoryExecution pe = productCategoryService.batchAddProductCategory(productCategoryList);
				if(pe.getState() != 1) {
					modelMap.put("success", false);
					modelMap.put("errMsg", pe.getStateInfo());
					return modelMap;
				} else {
					modelMap.put("success", true);
					return modelMap;
				}
			} catch (Exception e) {
				modelMap.put("success", false);
				modelMap.put("errMsg", e.getMessage());
				return modelMap;
			}
		}
	}
	
	@RequestMapping(value = "/removeproductcategory", method = RequestMethod.POST)
	@ResponseBody
	private Map<String, Object> removeProductCategory(Long productCategoryId, HttpServletRequest request) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		Shop currentShop = (Shop) request.getSession().getAttribute("currentShop");
		if (productCategoryId == null || productCategoryId <= 0) {
			modelMap.put("success", false);
			modelMap.put("errMsg", "提交的商品类别为空");
			return modelMap;
		} else {
			try {
				ProductCategoryExecution pce = productCategoryService.deleteProductCategory(productCategoryId, currentShop.getShopId());
				if(pce.getState() != 1) {
					modelMap.put("success", false);
					modelMap.put("errMsg", pce.getStateInfo());
					return modelMap;
				} else {
					modelMap.put("success", true);
					return modelMap;
				}
			} catch (Exception e) {
				modelMap.put("success", false);
				modelMap.put("errMsg", e.getMessage());
				return modelMap;
			}			
		}
	}
	
	
	@RequestMapping(value = "/getproductlistbyshop", method = RequestMethod.GET)
	@ResponseBody
	private Map<String, Object> getProductListByShop(HttpServletRequest request) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		int pageIndex = HttpServletRequestUtil.getInt(request, "pageIndex");
		int pageSize = HttpServletRequestUtil.getInt(request, "pageSize");
		Shop currentShop = (Shop) request.getSession().getAttribute("currentShop");
		Product productCondition = new Product();
		productCondition.setShop(currentShop);
		try {
			ProductExecution pe = productService.getProductList(productCondition, pageIndex, pageSize);
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
	
	@RequestMapping(value = "/getproductbyid", method = RequestMethod.GET)
	@ResponseBody
	private Map<String, Object> getProductById(Long productId, HttpServletRequest request) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		Product product = new Product();
		List<ProductCategory> productCategoryList = new ArrayList<ProductCategory>();
		if (productId <= 0 || productId == null) {
			modelMap.put("success", false);
			modelMap.put("errMsg", "所请求商品为空");
			return modelMap;
		} else {
			try {
				product = productService.getProductById(productId);
				productCategoryList = productCategoryService.getProductCategoryList(product.getShop().getShopId());
				if (product.getProductName() == null || "".equals(product.getProductName())) {
					modelMap.put("success", false);
					modelMap.put("errMsg", "所请求商品不存在");
					return modelMap;
				} else {
					modelMap.put("success", true);
					modelMap.put("product", product);
					modelMap.put("productCategoryList", productCategoryList);
					return modelMap;
				}
			} catch (Exception e) {
				modelMap.put("success", false);
				modelMap.put("errMsg", e.getMessage());
				return modelMap;				
			}
		}
	}
	
	@RequestMapping(value = "/modifyproduct", method = RequestMethod.POST)
	@ResponseBody
	private Map<String, Object> modifyProdut(HttpServletRequest request) throws IOException {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		boolean statusChange = HttpServletRequestUtil.getBoolean(request, "statusChange");
		// 判断验证码
		if (!CodeUtil.checkVerifyCode(request) && !statusChange) {
			modelMap.put("success", false);
			modelMap.put("errMsg", "输入了错误的验证码");
			return modelMap;
		}
		// 将JSON转换成POJO对象
		String productStr = HttpServletRequestUtil.getString(request, "productStr");
		ObjectMapper mapper = new ObjectMapper();
		Product product = new Product();
		try {
			product = mapper.readValue(productStr, Product.class);
		} catch (Exception e) {
			modelMap.put("success", false);
			modelMap.put("errMsg", e.getMessage());
			return modelMap;
		}
		Shop shop = (Shop) request.getSession().getAttribute("currentShop");
		product.setShop(shop);
		// 判断收到的对象是否为空对象
		if (product == null || product.getProductId() == null) {
			modelMap.put("success", false);
			modelMap.put("errMsg", "请求失败");
			return modelMap;
		} else {
			ProductExecution pe;
			// 判断是否是上架下架操作
			if (statusChange) {
				// 获取当前上架下架状态
				Product tempProduct = productService.getProductById(product.getProductId());
				if (tempProduct.getEnableStatus() == 0) {
					tempProduct.setEnableStatus(1);
				} else {
					tempProduct.setEnableStatus(0);
				}
				// 进行上架下架操作
				try {
					pe = productService.modifyProduct(tempProduct, null, null);
					if (pe.getState() != 1) {
						modelMap.put("success", false);
						modelMap.put("errMsg", pe.getStateInfo());
						return modelMap;
					} else {
						modelMap.put("success", true);
						return modelMap;
					}
				} catch (Exception e) {
					modelMap.put("success", false);
					modelMap.put("errMsg", e.getMessage());
					return modelMap;
				}
			} else {
				// 编辑商品操作
				ImageHolder thumbnail = null;
				List<ImageHolder> productImgHolderList = null;
				CommonsMultipartFile thumbnailFile = null;
				CommonsMultipartResolver commonsMultipartResolver = new CommonsMultipartResolver(
						request.getSession().getServletContext());
				if (!commonsMultipartResolver.isMultipart(request)) {
					modelMap.put("success", false);
					modelMap.put("errMsg", "上传图片不能为空");
					return modelMap;
				} else {
					// 获取缩略图图片
					MultipartHttpServletRequest multipartHttpServletRequest = (MultipartHttpServletRequest) request;
					thumbnailFile = (CommonsMultipartFile) multipartHttpServletRequest.getFile("thumbnail");
					if (thumbnailFile != null) {
						try {
							thumbnail = new ImageHolder(thumbnailFile.getOriginalFilename(), thumbnailFile.getInputStream());						
						} catch (IOException e) {
							modelMap.put("success", false);
							modelMap.put("errMsg", e.getMessage());
							return modelMap;
						} catch (Exception e) {
							modelMap.put("success", false);
							modelMap.put("errMsg", e.getMessage());
							return modelMap;
						}						
					}
					// 获取详情图图片, 暂时将图片数量上限定在30张
					CommonsMultipartFile productImgFile = (CommonsMultipartFile) multipartHttpServletRequest.getFile("productImg" + 0);
					productImgHolderList = new ArrayList<ImageHolder>();
					if (productImgFile != null) {
						for (int i=0; i<30; i++) {
							productImgFile = (CommonsMultipartFile) multipartHttpServletRequest.getFile("productImg" + i);
							if (productImgFile == null) {
								break;
							} else {
								productImgHolderList.add(
										new ImageHolder(productImgFile.getOriginalFilename(), productImgFile.getInputStream()));
							}
						}						
					}
					// 进行商品编辑操作
					try {
						pe = productService.modifyProduct(product, thumbnail, productImgHolderList);
						if (pe.getState() != 1) {
							modelMap.put("success", false);
							modelMap.put("errMsg", pe.getStateInfo());
							return modelMap;
						} else {
							modelMap.put("success", true);
							return modelMap;
						}
					} catch (Exception e) {
						modelMap.put("success", false);
						modelMap.put("errMsg", e.getMessage());
						return modelMap;
					}
					
				}
				
			}
		}
		
	}
}
