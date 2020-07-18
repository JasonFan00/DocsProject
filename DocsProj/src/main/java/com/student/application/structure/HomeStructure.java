package com.student.application.structure;

import java.io.File;
import java.util.*;

import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.student.application.PostPropertyConfig;

/**
 * Class to hold the list representing the structure of the home page
 * @author Jason Fan
 *
 */

@Component
public class HomeStructure {
	
	@Autowired
	PostPropertyConfig config;
	
	private List<Category> categories;
	
	public HomeStructure() {
		this.categories = new ArrayList<>();
	}
	
	public List<Category> getCategories() {
		return this.categories;
	}
	
	public String hello(){
		return null;
	}
	
	private void structure(File file, Category category) {
		if (!file.exists()) return;
		if (file.isDirectory()) {
			File[] files = file.listFiles();
			for (int i = 0; i < files.length; i++) {
				if (files[i].isDirectory()) {
					// Make a new home category and add it to parent
					Category categoryNew = new Category();
					category.addChildCategory(categoryNew);
					// Continue recursion with newly made one to fill that with any subcategories/items too
					structure(file, categoryNew);
				} else {
					//  Check if .html, if so then make a new subcategory
					if (FilenameUtils.getExtension(file.getName()).equals("html")) {
						//  For future can Use jsoup to grab some more info about the page, also get the url it is mapped to as well as make a number that it represents.  For now just make all numbers 0.0
						//  for now itemname is based off name of file, but to acommodate spaces in future will need to modify controler since spaces can't go in url
						String itemName = file.getName();
						String absPath = file.getAbsolutePath();
						String path = absPath.substring(absPath.indexOf("repo") + 6); //  Want to get the substring starting after the /repo/ in the abs path
						String url = "/repo/" + path;  //  to get to a repo page url path always starts with /repo/
						Double num = 0.0;
						CategoryItem item = new CategoryItem(itemName, url, num);
						category.addItem(item);
					}
				}
			}
		}
	}
	
	public void updateStructure() {
		File repoRoot;
		try {
			repoRoot = new File(config.getRepoPath());
		} catch (Exception e) {
			System.out.println("Unable to open repo file directory for structure: \n" + e.getStackTrace());
		}
		// To do...from the local repo root now construct the structure.  Each directory that has .md/.html files is a category.  Should consider making sub-sub-sub... categories (directories within directories that may/may not contain .md/.html files ("pages").
		
		
	}
}
