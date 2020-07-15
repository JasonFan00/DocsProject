package com.student.application.structure;

/**
 * This class represents the SubCategories that contain a name, an identifying number, and url to the html conversion to a 
 * markdown file
 * */
public class SubCategory {
	
	private String subCategoryName;
	private String url;
	private double num;
	
	public String getsubCategoryName() {
		return this.subCategoryName;
	}
	
	public String getSubCategoryUrl() {
		return this.url;
	}
	
	public double getSubCategoryNum() {
		return this.num;
	}
	
	public void setSubCategoryName(String newName) {
		this.subCategoryName = newName;
	}
	
	public void setSubCategoryUrl(String newUrl) {
		this.url = newUrl;
	}
	
	public void setSubCategoryNum(double newNum) {
		this.num = newNum;
	}
}
