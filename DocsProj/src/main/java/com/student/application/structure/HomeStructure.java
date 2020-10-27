package com.student.application.structure;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FilenameFilter;
import java.io.IOException;
import java.nio.file.Files;
import java.util.*;

import org.apache.commons.io.FileUtils;
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
	
	@Value("${numberSeparatorStr}")
	private String NUMBER_SEPARATOR_STR;
	
	private Integer MAX_ELES_PER_ROW; // maximum number of category items per row , got here bc this class is a bean then passed to the non-bean category via constructor
	
	private Category rootCategory;
	
	public HomeStructure(@Value("${core.max-cols-per-category}") Integer MAX_ELES_PER_ROW) {  //  Doing this as argument because if it's class property like numberSepStr, then injection is done AFTER constructor is called, so if it is used in constructor is null (had an issue with this)
		this.MAX_ELES_PER_ROW = MAX_ELES_PER_ROW;
		this.rootCategory = new Category("ROOT", MAX_ELES_PER_ROW);
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
						
						String catDescriptor = null;
						File[] catFiles = files[i].listFiles(new FilenameFilter() {
							@Override
							public boolean accept(File dir, String name) { // anonymous class implementation for FilenameFilter interface, maybe refactor code later in places where this can be used instead of looping through list files
								return name.toLowerCase().equals("categorydescriptor.txt") && name.toLowerCase().endsWith(".txt");
							}
						});
						
						if (catFiles.length > 0) {
							File descriptorFile = catFiles[0];
							try {
								catDescriptor = FileUtils.readFileToString(descriptorFile, "UTF-8");
							} catch (IOException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}
						
						Category categoryNew = new Category(files[i].getName(), catDescriptor, MAX_ELES_PER_ROW);
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
			String filNameFull = file.getName();
			if (FilenameUtils.getExtension(filNameFull).equals("html")) {
				//  For future can Use jsoup to grab some more info about the page, also get the url it is mapped to as well as make a number that it represents.  For now just make all numbers 0.0
				//  for now itemname is based off name of file, but to acommodate spaces in future will need to modify controler since spaces can't go in url, can use some character and replace
				String[] itemNameParts = filNameFull.split(NUMBER_SEPARATOR_STR);
				String fileNameOnly = FilenameUtils.removeExtension(itemNameParts[1]);
				String fileNameLabel = itemNameParts[0];
				String absPath = FilenameUtils.getPath(file.getAbsolutePath().toLowerCase()); // Get path but no file name itself at end, added below
				String path = absPath.substring(absPath.indexOf("repo") + 5); //  Want to get the substring starting after the /repo/ in the abs path
				String url = ("/repo/" + path + fileNameOnly.toLowerCase()).replace(' ', '-');  //  to get to a repo page url path will always starts with /repo/.  This line is a mess to read, will cleanup later
				CategoryItem item = new CategoryItem(fileNameOnly, url, fileNameLabel);
				category.addItem(item);
			}
		}
	}
	
	/**
	 * Recursively sorts the category items lists
	 */
	private void sortCategoryItems(Category category) {
		
		for (ArrayList<CategoryItem> rowItemList : category.getItems()) {
			Collections.sort(rowItemList);
		}
		
		for (Category childCat : category.getChildCategories()) {
			sortCategoryItems(childCat);
		}
	}
	
	/**
	 * Prints out the structure
	 * @param sb
	 * @param category
	 * @param level
	 */
	private void printStructure(StringBuilder sb, Category category, int level) {
		sb.append("\n");
		
		StringBuilder indentSB = new StringBuilder();
		for (int i = 0; i < level; i++) {
			indentSB.append("\t");
		}
		String indents = indentSB.toString();
		sb.append(indents);
		sb.append(category.getCatName()); //  Add directory name
		List<ArrayList<CategoryItem>> categoryItemsGrid = category.getItems();
		if (categoryItemsGrid.size() > 0) sb.append("\n");
		
		for (int i = 0; i < categoryItemsGrid.size(); i++) {
			List<CategoryItem> row = categoryItemsGrid.get(i);
			for (CategoryItem item : row) {
				sb.append("\t" + indents + "+" + item.getLabel() + " " +item.getItemName());  //  Add file name, always get an extra indent
				if (i != categoryItemsGrid.size() - 1) sb.append("\n"); // avoid extra unnecessary empty line for last elements
			}
			
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
		
		// Each directory that has .md/.html files is a category. Can do sub-sub-sub... categories (directories within directories that may/may not contain .md/.html files ("pages").
		// start at the top level
		this.rootCategory = new Category("ROOT", MAX_ELES_PER_ROW); //  could some spring IoC thing manage local variables? Since new will couple it more
		this.updateStructure(repoRoot, this.rootCategory); //  get any child categories and such
		this.sortCategoryItems(this.rootCategory);  //  traverse the now in memory representation of the file structure and sort child category items
		this.printStructure();
	}
}
