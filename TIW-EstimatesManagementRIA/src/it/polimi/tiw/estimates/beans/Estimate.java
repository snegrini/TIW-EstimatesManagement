package it.polimi.tiw.estimates.beans;

public class Estimate {
	private int id;
	private float price;
	private Product product;
	private User customer;
	private User employee;

	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
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
	
	public User getCustomer() {
		return customer;
	}
	
	public void setCustomer(User customer) {
		this.customer = customer;
	}
	
	public User getEmployee() {
		return employee;
	}
	
	public void setEmployee(User employee) {
		this.employee = employee;
	}

}