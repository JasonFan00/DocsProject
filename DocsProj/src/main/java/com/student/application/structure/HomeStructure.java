package com.student.application.structure;

import java.util.*;

import org.springframework.stereotype.Component;

/**
 * Class to hold the list representing the structure of the home page
 * @author Jason Fan
 *
 */

@Component
public class HomeStructure {
	private List<HomeCategory> categories = new ArrayList<>();
	
	public List<HomeCategory> getCategories() {
		return this.categories;
	}
	
	public String hello(){
		return null;
	}
}
