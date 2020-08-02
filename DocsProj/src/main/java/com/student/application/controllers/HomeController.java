package com.student.application.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import com.student.application.structure.HomeStructure;

/**
 * Controller that currently just handles a request to the WIP home-page
 * @author Jason Fan
 *
 */
@Controller
public class HomeController {
	
	@Autowired
	HomeStructure HomeStructure;
	
	@RequestMapping("/")
	public String homeController(Model model) {
		this.HomeStructure.updateStructure(); //  For now update at every request, might solve occasional bug where result of thymeleaf processing blank (no child+ categories from root).  Probably because at app startup template gets processed, even before structure gets updated?  Also look into if need to disable thymeleaf cache
		model.addAttribute("greeting", "Hello World!"); //  actually for above probs not since if leave error in template it does not happen until request, so no processing at startup?
		model.addAttribute("structure", this.HomeStructure.getRootCategory());
		return "index";
	}
}
