package it.polimi.tiw.estimates.beans;

public class Estimate {
	private int id;
	private int client; 
	private int employee;
	private float price;
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	
	public int getClient() {
		return client;
	}
	public void setClient(int client) {
		this.client = client;
	}
	
	public int getEmployee() {
		return employee;
	}
	public void setEmployee(int employee) {
		this.employee = employee;
	}
	
	public float getPrice() {
		return price;
	}
	public void setPrice(float price) {
		this.price = price;
	}
}