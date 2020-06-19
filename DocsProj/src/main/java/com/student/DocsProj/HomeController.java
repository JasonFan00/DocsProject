package com.student.DocsProj;

import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

//  Controller for home page currently (non-rest files/template files)
@Controller
public class HomeController {
	@RequestMapping("/")
	public String homeController(HttpServletRequest request) {
		return "index";
	}
}
