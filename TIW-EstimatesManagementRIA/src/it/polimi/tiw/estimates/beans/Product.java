package it.polimi.tiw.estimates.beans;

import java.util.List;

public class Product {
	private int id;
	private String name;
	private String image;
	private List<Optional> optionals;
	
	public int getId() {
		return id;
	}
	
	public void setId(int id) {
		this.id = id;
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public String getImage() {
		return image;
	}
	
	public void setImage(String image) {
		this.image = image;
	}
	
	public List<Optional> getOptionals() {
		return optionals;
	}
	
	public void setOptionals(List<Optional> o) {
		this.optionals = o;
	}
	
	
}
