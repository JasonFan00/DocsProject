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
		model.addAttribute("structure", this.HomeStructure.getRootCategory());
		model.addAttribute("level", 0);
		
		
		
		return "index";
	}
}
