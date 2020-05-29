package it.polimi.tiw.estimates.beans;

public class Estimate {
	private int id;
	private int employeeId;
	private float price;
	private Product product;
	private User client;
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}

	public int getEmployeeId() {
		return employeeId;
	}
	public void setEmployeeId(int employeeId) {
		this.employeeId = employeeId;
	}
	
	public float getPrice() {
		return price;
	}
	public void setPrice(float price) {
		this.price = price;
	}
	public Product getProduct() {
		return product;
	}
	
	public void setProduct(Product product) {
		this.product = product;
	}
	
	public User getClient() {
		return client;
	}
	public void setClient(User client) {
		this.client = client;
	}
	

}