package it.polimi.tiw.estimates.daos;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import it.polimi.tiw.estimates.beans.Estimate;
import it.polimi.tiw.estimates.beans.Product;
import it.polimi.tiw.estimates.beans.User;

public class EstimateDAO {
	private Connection connection;

	public EstimateDAO(Connection connection) {
		this.connection = connection;
	}
	

	public int createEstimate(int customerId, int productId, String[] optionalsId) throws SQLException  {
		String query = "INSERT into estimate (usrid, prdid) VALUES(?, ?)";
		int genEstimateId;
		
		//https://stackoverflow.com/questions/1915166/how-to-get-the-insert-id-in-jdbc
		try (PreparedStatement pstatement = connection.prepareStatement(query,  Statement.RETURN_GENERATED_KEYS);) {	
			pstatement.setInt(1, customerId);
			pstatement.setInt(2, productId);
			pstatement.executeUpdate();
			
			try (ResultSet generatedKeys = pstatement.getGeneratedKeys()) {
	            if (generatedKeys.next()) {
	            	genEstimateId = generatedKeys.getInt(1);
	            }else {
	                throw new SQLException("Creating user failed, no ID obtained.");
	            }
	        }
		}
		
		
		for (int i = 0; i < optionalsId.length; i++) {
			query = "INSERT into chosenoptional (estid, optid) VALUES(?, ?)";
			try (PreparedStatement pstatement = connection.prepareStatement(query);) {
				pstatement.setInt(1, genEstimateId);
				pstatement.setInt(2, Integer.parseInt(optionalsId[i]));
				pstatement.executeUpdate();
			}
		}
		
		return genEstimateId;
	}
	
	public boolean addEstimatePrice(int employeeId, int estimateId, float price) throws SQLException {
		String query = "UPDATE estimate SET price=?, empid=? WHERE id=?";
		try (PreparedStatement pstatement = connection.prepareStatement(query);) {
			pstatement.setFloat(1, price);
			pstatement.setInt(2, employeeId);
			pstatement.setInt(3, estimateId);
			pstatement.executeUpdate();
		}
		return false;
	}
	
	
	/**
	 * Returns a list of all of the estimates of a customer, given the id.
	 */
	public List<Estimate> findEstimatesByCustomer(int customerId) throws SQLException {
		List<Estimate> estimates = new ArrayList<>();
		String query = 	"SELECT e.id, e.price, p.name "
				+ "FROM estimate AS e, product AS p "
				+ "WHERE e.prdid = p.id "
				+ "AND e.usrid = ? "
				+ "ORDER BY e.id ASC";
		
		try (PreparedStatement pstatement = connection.prepareStatement(query);) {
			
			pstatement.setInt(1, customerId);
			
			try (ResultSet result = pstatement.executeQuery();) {
				while (result.next()) {
					Estimate estimate = new Estimate();
					
					Product product = new Product();
					product.setName(result.getString("name"));
					
					estimate.setId(result.getInt("id"));
					estimate.setPrice(result.getFloat("price"));
					estimate.setProduct(product);

					estimates.add(estimate);
				}
			}
		}
		return estimates;
	}

	
	public List<Estimate> findPricedEstimatesByEmployee(int employee) throws SQLException {
		List<Estimate> estimates = new ArrayList<>();
		
		String query = "SELECT id, usrid, prdid, empid, price FROM estimate WHERE empid = ?";
		
		try (PreparedStatement pstatement = connection.prepareStatement(query);) {
						
			pstatement.setInt(1, employee);
			
			try (ResultSet result = pstatement.executeQuery();) {
				while (result.next()) {
					Estimate estimate = new Estimate();
					User user = new User();
					user.setId(result.getInt("empid"));
					
					estimate.setId(result.getInt("id"));
					estimate.setPrice(result.getFloat("price"));
					estimate.setEmployee(user);
					
					estimates.add(estimate);
				}
			}
		}
		return estimates;
	}
	
	public List<Estimate> findNonPricedEstimates() throws SQLException {
		List<Estimate> estimates = new ArrayList<>();
		
		String query = "SELECT id FROM estimate WHERE empid IS NULL AND price IS NULL";
		
		try (PreparedStatement pstatement = connection.prepareStatement(query);) {
						
			try (ResultSet result = pstatement.executeQuery();) {
				
				while (result.next()) {
					Estimate estimate = new Estimate();
										
					estimate.setId(result.getInt("id"));
					estimates.add(estimate);
				}
			}
		}
		return estimates;
	}
	
	public Estimate findEstimateById(int estimateId) throws SQLException {
		Estimate estimate = null;

		String query = "SELECT id, price FROM estimate WHERE id = ?";
		try (PreparedStatement pstatement = connection.prepareStatement(query);) {
			pstatement.setInt(1, estimateId);
			try (ResultSet result = pstatement.executeQuery();) {
				if (result.next()) {
					estimate = new Estimate();
					
					estimate.setId(result.getInt("id"));
					estimate.setPrice(result.getFloat("price"));
				}
			}
		}
		return estimate;
	}
	
	public void changeEstimatePrice(int estimateId, int employeeId, float price) throws SQLException {
		String query = "UPDATE estimate SET empid = ?, price = ? WHERE id = ?";
		
		try (PreparedStatement pstatement = connection.prepareStatement(query);) {
			pstatement.setInt(1, employeeId);
			pstatement.setFloat(2, price);
			pstatement.setInt(3, estimateId);
			pstatement.executeUpdate();
		}
	}


	public Estimate findEstimateByIdAndCustomer(int estimateId, int customerId) throws SQLException {
		Estimate estimate = null;

		String query = "SELECT id, price FROM estimate WHERE id = ? AND usrid = ?";
		
		try (PreparedStatement pstatement = connection.prepareStatement(query);) {
			pstatement.setInt(1, estimateId);
			pstatement.setInt(2, customerId);
			
			try (ResultSet result = pstatement.executeQuery();) {
				if (result.next()) {
					estimate = new Estimate();
					
					estimate.setId(result.getInt("id"));
					estimate.setPrice(result.getFloat("price"));
				}
			}
		}
		return estimate;
	}

}
