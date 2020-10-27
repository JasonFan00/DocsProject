package com.student.application.structure;

import java.util.ArrayList;
import java.util.List;


/**
 * Each HomeCategory object represents a category on the home page
 * @author Jason Fan
 *
 */


public class Category {
	private List<Category> childrenCategories;
	private List<ArrayList<CategoryItem>> items;  //  items is really a 2d grid of items, as the display structure of category items in the web page is built here then added to the model, then thymeleaf simply traverses it.  Bc thymeleaf has some limitations (can't really have a counter variable bc of scoping)
	private String catName;
	private String catDescriptor;
	
	private int maxElesPerRow;  //  How many category items can be in each row, stored in app properties
	
	public Category(String name, int maxElesPerRowProperty) {  //  maxElesPerRow stored in app properties, this class not managed by spring (not a bean) so can't use @value directly
		this.maxElesPerRow = maxElesPerRowProperty;
		this.childrenCategories = new ArrayList<>();
		this.items = new ArrayList<ArrayList<CategoryItem>>();
		this.catName = name;
	}
	
	public Category(String name, String catDescriptor, int maxElesPerRowProperty) {
		this(name, maxElesPerRowProperty);
		this.catDescriptor = catDescriptor;
	}
	
	public void addChildCategory(Category category) {
		this.childrenCategories.add(category);
	}
	
	public void addItem(CategoryItem item) {
		boolean assigned = false;
		for (int i = 0; i < this.items.size(); i++) {
			List<CategoryItem> rowsList = this.items.get(i);
			if (rowsList.size() < maxElesPerRow) {
				rowsList.add(item);
				assigned = true;
				break;
			}
		}
		
		if (!assigned) {
			ArrayList<CategoryItem> newRowsList = new ArrayList<CategoryItem>();
			newRowsList.add(item);
			this.items.add(newRowsList);
		}
	}
	
	public List<Category> getChildCategories() {
		return this.childrenCategories;
	}
	
	public List<ArrayList<CategoryItem>> getItems() {
		return this.items;
	}
	
	public String getCatName() {
		return this.catName;
	}
	
	public void setCatDescriptor(String catDescriptor) {
		this.catDescriptor = catDescriptor;
	}
	
	public String getCatDescriptor() {
		return this.catDescriptor;
	}
}
