package com.appleyk.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class PageIndexController {

	/**
	 * 如果出现thymeleaf报错，则不要使用/
	 * @param page
	 * @return
	 */
	
	@RequestMapping("file")
	public String file() {		
		return "file";
	}
	
	@RequestMapping("/multifile")
	public String multifile() {		
		return "multifile";
	}
	
	@RequestMapping("")
	public String index() {
		return "index";
	}
}
