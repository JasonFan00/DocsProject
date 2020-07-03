package com.student.DocsProj;

import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Controller that currently just handles a request to the WIP home-page
 * @author Jason Fan
 *
 */
@Controller
public class HomeController {
	@RequestMapping("/")
	public String homeController(Model model) {
		model.addAttribute("message", "Hello Spring MVC 5!");
		return "index";
	}
}
