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
	private List<CategoryItem> items;
	
	public Category() {
		this.childrenCategories = new ArrayList<>();
		this.items = new ArrayList<>();
	}
	
	public void addChildCategory(Category category) {
		this.childrenCategories.add(category);
	}
	
	public void addItem(CategoryItem item) {
		this.items.add(item);
	}
	
	public List<Category> getChildCategories() {
		return this.childrenCategories;
	}
	
	public List<CategoryItem> getCategoryItems() {
		return this.items;
	}
}
