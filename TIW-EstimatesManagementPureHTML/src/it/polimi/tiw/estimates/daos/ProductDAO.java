package it.polimi.tiw.estimates.daos;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import it.polimi.tiw.estimates.beans.Estimate;
import it.polimi.tiw.estimates.beans.Optional;
import it.polimi.tiw.estimates.beans.OptionalType;
import it.polimi.tiw.estimates.beans.Product;

public class ProductDAO {
	private Connection connection;

	public ProductDAO(Connection connection) {
		this.connection = connection;
	}
	
	public List<Product> findProducts() throws SQLException {
		List<Product> products = new ArrayList<>();
		
		// Only products with at least one optional
		String query = "SELECT DISTINCT p.id, p.name "
				+ "FROM product AS p, optionaltoproduct as otp "
				+ "WHERE p.id = otp.prdid";
		
		try (PreparedStatement pstatement = connection.prepareStatement(query);) {
			
			try (ResultSet result = pstatement.executeQuery();) {	
				
				while (result.next()) {
					Product product = new Product();
					product.setId(result.getInt("id"));
					product.setName(result.getString("name"));
					products.add(product);
				}
				
			}
			
		}
		
		return products;
	}
	
	
	public Product findProductById(int productId) throws SQLException {
		Product product = null;
		
		String query = "SELECT p.id, p.name, p.image "
				+ "FROM product AS p "
				+ "WHERE p.id = ?";
		
		try (PreparedStatement pstatement = connection.prepareStatement(query);) {
			
			pstatement.setInt(1, productId);
			
			try (ResultSet result = pstatement.executeQuery();) {	
				
				while (result.next()) {
					product = new Product();
					product.setId(result.getInt("id"));
					product.setName(result.getString("name"));
					product.setImage(result.getString("image"));
				}
				
			}
			
		}
		
		return product;
	}
	
	/*
	 * returns a list of product given the ID in the 'estimate' table
	 * */
	public List<Product> findProductsByProductID(List<Estimate> e) throws SQLException {
		List<Product> products = new ArrayList<>();
		List<Estimate> est = e;
		
		// Only products with at least one optional
		String query =	"SELECT p.id,p.name " + 
						"FROM estimate AS e, product AS p " + 
						"WHERE p.id = ?";
		
		try (PreparedStatement pstatement = connection.prepareStatement(query);) {
			
			for(int i=0; i<est.size();i++) {
				int pID = est.get(i).getProductId();
				pstatement.setInt(1, pID);
				
				try (ResultSet result = pstatement.executeQuery();) {
					while (result.next()) {
						Product product = new Product();
						
						product.setId(result.getInt("id"));
						product.setName(result.getString("name"));
						//product.setImage("image");
						products.add(product);
					}
				}
			}
			
		}
		return products;
	}
	
	public List<Product> findPricedProductsByEmployee(int employeeId) throws SQLException {
		List<Product> products = new ArrayList<>();
		
		String query = "SELECT DISTINCT p.id, p.name "
				+ "FROM product AS p, estimate AS e "
				+ "WHERE p.id = e.prdid "
				+ "AND e.empid = ?";
		
		try (PreparedStatement pstatement = connection.prepareStatement(query);) {
						
			pstatement.setInt(1, employeeId);
			
			try (ResultSet result = pstatement.executeQuery();) {
				while (result.next()) {
					Product product = new Product();
					
					product.setId(result.getInt("id"));
					product.setName(result.getString("name"));
					products.add(product);
				}
			}
		}
		return products;
	}
	
	public List<Product> findNonPricedProducts() throws SQLException {
		List<Product> products = new ArrayList<>();
		
		String query = "SELECT DISTINCT p.id, p.name "
				+ "FROM product AS p, estimate AS e "
				+ "WHERE p.id = e.prdid "
				+ "AND e.empid IS NULL AND e.price IS NULL";
		
		try (PreparedStatement pstatement = connection.prepareStatement(query);) {
									
			try (ResultSet result = pstatement.executeQuery();) {
				while (result.next()) {
					Product product = new Product();
					
					product.setId(result.getInt("id"));
					product.setName(result.getString("name"));
					products.add(product);
				}
			}
		}
		return products;
	}
	
}
