package com.student.application.structure;

/**
 * This class represents the SubCategories that contain a name, an identifying number, and url to the html conversion to a 
 * markdown file
 * */
public class CategoryItem {
	
	private String itemName;
	private String url;
	private double num;
	
	public CategoryItem(String itemName, String url, double num) {
		this.itemName = itemName;
		this.url = url;
		this.num = num;
	}
	
	
	public String getItemName() {
		return this.itemName;
	}
	
	public String getUrl() {
		return this.url;
	}
	
	public double getSubCategoryNum() {
		return this.num;
	}
	
	public void setItemName(String newName) {
		this.itemName = newName;
	}
	
	public void setItemUrl(String newUrl) {
		this.url = newUrl;
	}
	
	public void setItemNum(double newNum) {
		this.num = newNum;
	}
}
