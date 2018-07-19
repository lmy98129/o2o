package com.imooc.o2o.web.superadmin;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * 主要用来解析路由并转发到相应的html中
 * 
 *
 */
@Controller
@RequestMapping(value = "/frontend", method = RequestMethod.GET)
public class FrontendAdminController {
	/**
	 * 转发至首页
	 * @return String
	 */

	@RequestMapping(value = "/index")
	public String frontendIndex() {
		return "frontend/index";
	}
	
	@RequestMapping(value = "/shoplist")
	public String shopList() {
		return "frontend/shoplist";
	}
	
	@RequestMapping(value = "/shopdetail")
	public String shopDetail() {
		return "frontend/shopdetail";
	}
	
	@RequestMapping(value = "/productdetail")
	public String productDetail() {
		return "frontend/productdetail";
	}
}