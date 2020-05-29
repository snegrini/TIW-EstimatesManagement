package it.polimi.tiw.estimates.daos;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

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
	
	public List<Optional> findOptionalsByProduct(int productId) throws SQLException {
		List<Optional> optionals = new ArrayList<>();
		
		// Only products with at least one optional
		String query = "SELECT o.id, o.name, o.type"
				+ "FROM optional AS o, optionaltoproduct as otp "
				+ "WHERE o.id = otp.optid "
				+ "AND otp.prdid = ?";
				
		try (PreparedStatement pstatement = connection.prepareStatement(query);) {
			
			pstatement.setInt(1, productId);
			
			try (ResultSet result = pstatement.executeQuery();) {
				while (result.next()) {
					Optional optional = new Optional();
					optional.setId(result.getInt("id"));
					optional.setName(result.getString("name"));
					optional.setType(OptionalType.valueOf(result.getString("type")));
					optionals.add(optional);
				}
			}
		}
		return optionals;
	}
	
}