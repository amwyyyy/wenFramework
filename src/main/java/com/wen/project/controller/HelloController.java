package com.wen.project.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.wen.framework.controller.BaseController;
import com.wen.framework.service.BaseService;
import com.wen.project.dto.UserDto;
import com.wen.project.service.UserService;

@Controller
@RequestMapping("/hello")
public class HelloController extends BaseController<UserDto, Integer> {
	
	@Autowired
	private UserService userService;
	
	@Override
	protected BaseService<UserDto, Integer> getDefaultService() {
		return userService;
	}
	
	@RequestMapping(value="/world")
	@ResponseBody
	public String test() {
//		return userService.findAll();
		return "AAA你";
	}
	
	@RequestMapping(value="/world2")
	@ResponseBody
	public List<UserDto> test1() {
		return userService.findAll();
	}
	
	@RequestMapping(value="/add")
	@ResponseBody
	public void add(UserDto user) {
		userService.insert(user);
	}
}
