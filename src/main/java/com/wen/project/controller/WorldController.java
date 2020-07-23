package com.wen.project.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.wen.framework.controller.BaseController;
import com.wen.framework.service.BaseService;
import com.wen.framework.utils.ThreadPool;
import com.wen.project.dto.OrderDto;
import com.wen.project.service.OrderService;

@Controller
@RequestMapping("world")
public class WorldController extends BaseController<OrderDto, Integer>{
	@Autowired
	private OrderService orderService;

	@Override
	protected BaseService<OrderDto, Integer> getDefaultService() {
		return orderService;
	}
	
	@Autowired
	private ThreadPool pool;
	
	@RequestMapping("world.html")
	@ResponseBody
	public String test() {
		System.out.println(pool.getInstance().hashCode());
		System.out.println(pool.getInstance().getCorePoolSize());
		return "yes";
	}

}
