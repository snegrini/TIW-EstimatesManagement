package it.polimi.tiw.estimates.daos;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import it.polimi.tiw.estimates.beans.Product;

public class ProductDAO {
	private Connection connection;
	private int userID;

	public ProductDAO(Connection connection,int userID) {
		this.connection = connection;
		this.userID = userID;
	}
	
	public List<Product> findProducts() throws SQLException {
		List<Product> products = new ArrayList<>();
		
		// Only products with at least one optional
		String query = "SELECT p.id, p.name"
				+ "FROM product AS p, optionaltoproduct as otp"
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
	
}
