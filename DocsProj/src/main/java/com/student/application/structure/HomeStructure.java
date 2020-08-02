package com.student.application.structure;

import java.io.File;
import java.io.FileNotFoundException;
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
	
	private Category rootCategory;
	
	public HomeStructure() {
		this.rootCategory = new Category("ROOT");
	}
	
	public Category getRootCategory() {
		return this.rootCategory;
	}
	
	public String hello(){
		return null;
	}
	
	private void updateStructure(File file, Category category) {
		if (!file.exists() || file.getName().equals(".git")) return; // can ignore the .git from cloning remote repo
		if (file.isDirectory()) {
			File[] files = file.listFiles();
			for (int i = 0; i < files.length; i++) {
				if (files[i].isDirectory()) {
					if (!files[i].getName().equals(".git")) {
						// Make a new home category and add it to parent
						Category categoryNew = new Category(files[i].getName());
						category.addChildCategory(categoryNew);
						updateStructure(files[i], categoryNew);
					}
				} else {
					updateStructure(files[i], category); // update using current category level
				}
				// Continue recursion with newly made one to fill that with any subcategories/items too
			}
		} else {
			//  Check if file is .html, if so then make a new subcategory
			if (FilenameUtils.getExtension(file.getName()).equals("html")) {
				//  For future can Use jsoup to grab some more info about the page, also get the url it is mapped to as well as make a number that it represents.  For now just make all numbers 0.0
				//  for now itemname is based off name of file, but to acommodate spaces in future will need to modify controler since spaces can't go in url, can use some character and replace
				String itemName = file.getName();
				String absPath = FilenameUtils.removeExtension(file.getAbsolutePath()).toLowerCase(); // Don't want the extension in the url, and make everything lowercase in url path
				String path = absPath.substring(absPath.indexOf("repo") + 5); //  Want to get the substring starting after the /repo/ in the abs path
				String url = "/repo/" + path;  //  to get to a repo page url path always starts with /repo/
				Double num = 0.0;
				CategoryItem item = new CategoryItem(itemName, url, num);
				category.addItem(item);
			}
		}
	}
	
	// definitely better ways to do this, but works for now
	private void printStructure(StringBuilder sb, Category category, int level) {
		sb.append("\n");
		
		StringBuilder indentSB = new StringBuilder();
		for (int i = 0; i < level; i++) {
			indentSB.append("\t");
		}
		String indents = indentSB.toString();
		sb.append(indents);
		sb.append(category.getCatName()); //  Add directory name
		List<CategoryItem> categoryItems = category.getItems();
		if (categoryItems.size() > 0) sb.append("\n");
		for (int i = 0; i < categoryItems.size(); i++) {
			sb.append("\t" + indents + "+" + categoryItems.get(i).getItemName());  //  Add file name, always get an extra indent
			if (i != categoryItems.size() - 1) sb.append("\n"); // avoid extra unnecessary empty line for last elements
		}
		
		
		List<Category> childrenCategories = category.getChildCategories();
		for (Category cat : childrenCategories) {
			printStructure(sb, cat, level + 1);
		}
	}
	
	/**
	 * Just to confirm my recursion above is correct
	 */
	private void printStructure() {
		StringBuilder sb = new StringBuilder();
		printStructure(sb, this.rootCategory, 0);
		System.out.println(sb.toString());
	}
	
	public void updateStructure() {
		File repoRoot = null;
		try {
			repoRoot = new File(config.getRepoPath());
			
			if (!repoRoot.exists()) {
				throw new FileNotFoundException();
			}
		} catch (FileNotFoundException e) {
			System.out.println("Repo root file not found: \n" + e.getStackTrace());
		} catch (Exception e) {
			System.out.println("Unable to open repo file directory for structure: \n" + e.getStackTrace());
		}
		
		// To do...from the local repo root now construct the structure.  Each directory that has .md/.html files is a category.  Should consider making sub-sub-sub... categories (directories within directories that may/may not contain .md/.html files ("pages").
		// start at the top level
		this.rootCategory = new Category("ROOT"); //  could some spring IoC thing manage local variables? Since new will couple it more
		this.updateStructure(repoRoot, this.rootCategory); //  get any child categories and such
		this.printStructure();
		// To do...print out the result to make sure recursion correct
	}
}
