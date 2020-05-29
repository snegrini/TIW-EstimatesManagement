package it.polimi.tiw.estimates.daos;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import it.polimi.tiw.estimates.beans.Estimate;
import it.polimi.tiw.estimates.beans.Optional;
import it.polimi.tiw.estimates.beans.Product;
import it.polimi.tiw.estimates.beans.User;

public class EstimateDAO {
	private Connection connection;
	private int userID;

	public EstimateDAO(Connection connection, int userID) {
		this.connection = connection;
		this.userID = userID;
	}
	

	public boolean createEstimate(int productID, int[] optionals) throws SQLException  {
		//TODO: THE QUERY
		String query = "INSERT into estimate (usrid, prdid) VALUES(?, ?)";
		try (PreparedStatement pstatement = connection.prepareStatement(query);) {
			pstatement.setInt(1, userID);
			pstatement.setInt(2, productID);
			pstatement.executeUpdate();
		}
		// LAST_INSERT_ID() will return the last insert id from the current connection
		query = "INSERT into chosenoptional (estid, optid) VALUES(LAST_INSERT_ID(), ?)";
		try (PreparedStatement pstatement = connection.prepareStatement(query);) {
			pstatement.setInt(1, optionals[0]);
			pstatement.executeUpdate();
		}
		
		return false;
	}
	
	public List<Estimate> findPricedEstimatesByEmployee(int employee) throws SQLException {
		List<Estimate> estimates = new ArrayList<>();
		
		String query = "SELECT e.id, e.empid, e.price, p.name, u.username "
				+ "FROM estimate AS e, product as p, user AS u "
				+ "WHERE e.empid = u.id AND e.prdid = p.id "
				+ "AND empid = ? ";
		
		try (PreparedStatement pstatement = connection.prepareStatement(query);) {
						
			pstatement.setInt(1, employee);
			
			try (ResultSet result = pstatement.executeQuery();) {
				while (result.next()) {
					Estimate estimate = new Estimate();
					User client = new User();
					Product product = new Product();
					
					client.setUsername(result.getString("username"));
					
					product.setName(result.getString("name"));
					
					estimate.setId(result.getInt("id"));
					estimate.setEmployeeId(result.getInt("empid"));
					estimate.setPrice(result.getFloat("price"));
					estimate.setClient(client);
					estimate.setProduct(product);
					estimates.add(estimate);
				}
			}
		}
		return estimates;
	}
	
	public List<Estimate> findNonPricedEstimates() throws SQLException {
		List<Estimate> estimates = new ArrayList<>();
		
		String query = "SELECT e.id, e.usrid, e.empid, e.price, p.name "
				+ "FROM estimate AS e, product AS p "
				+ "WHERE e.prdid = p.id "
				+ "AND e.empid IS NULL AND e.price IS NULL";
		
		try (PreparedStatement pstatement = connection.prepareStatement(query);) {
						
			try (ResultSet result = pstatement.executeQuery();) {
				while (result.next()) {
					Estimate estimate = new Estimate();
					Product product = new Product();
					
					product.setName(result.getString("name"));
					
					estimate.setId(result.getInt("id"));
					estimate.setEmployeeId(result.getInt("empid"));
					estimate.setPrice(result.getFloat("price"));
					estimate.setProduct(product);
					estimates.add(estimate);
				}
			}
		}
		return estimates;
	}
}
