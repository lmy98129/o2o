package com.imooc.o2o.web.superadmin;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
@RequestMapping(value = "/frontend", method = RequestMethod.GET)
/**
 * 主要用来解析路由并转发到相应的html中
 * 
 *
 */
public class FrontendAdminController {
	/**
	 * 转发至首页
	 * @return String
	 */
	@RequestMapping("")
	public String frontendIndex() {
		return "frontend/index";
	}
}
