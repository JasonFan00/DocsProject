package com.student.application.structure;

/**
 * This class represents the SubCategories that contain a name, an identifying number, and url to the html conversion to a 
 * markdown file
 * */
public class CategoryItem implements Comparable<CategoryItem> {
	
	private String itemName;
	private String url;
	private String label;
	
	public CategoryItem(String itemName, String url, String newLabel) {
		this.itemName = itemName;
		this.url = url;
		this.label = newLabel;
	}
	
	
	public String getItemName() {
		return this.itemName;
	}
	
	public String getUrl() {
		return this.url;
	}
	
	public String getLabel() {
		return this.label;
	}
	
	public void setItemName(String newName) {
		this.itemName = newName;
	}
	
	public void setItemUrl(String newUrl) {
		this.url = newUrl;
	}
	
	public void setLabel(String newLabel) {
		this.label = newLabel;
	}

	
	@Override
	public int compareTo(CategoryItem otherItem) {
		return this.label.compareTo(otherItem.getLabel());  //  Sort by labels
	}
}
