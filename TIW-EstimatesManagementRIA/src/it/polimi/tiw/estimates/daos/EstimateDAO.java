package it.polimi.tiw.estimates.daos;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import it.polimi.tiw.estimates.beans.Estimate;

public class EstimateDAO {
	private Connection connection;
	private int userID;

	public EstimateDAO(Connection connection, int userID) {
		this.connection = connection;
		this.userID = userID;
	}
	

	public boolean createEstimate(int productID, String[] optionalsID) throws SQLException  {
		String query = "INSERT into estimate (usrid, prdid) VALUES(?, ?)";
		
		try (PreparedStatement pstatement = connection.prepareStatement(query);) {
			pstatement.setInt(1, userID);
			pstatement.setInt(2, productID);
			pstatement.executeUpdate();
		}
		
		// LAST_INSERT_ID() will return the last insert id from the current connection
		for (int i = 0; i< optionalsID.length; i++) {
			query = "INSERT into chosenoptional (estid, optid) VALUES(LAST_INSERT_ID(), ?)";
			try (PreparedStatement pstatement = connection.prepareStatement(query);) {
				pstatement.setInt(1, Integer.parseInt(optionalsID[i]));
				pstatement.executeUpdate();
			}
		}
		
		return false;
	}
	
	public boolean addEstimatePrice(int estimateID, float price) throws SQLException {
		String query = "UPDATE estimate SET price=?, empid=? WHERE id=?";
		try (PreparedStatement pstatement = connection.prepareStatement(query);) {
			pstatement.setFloat(1, price);
			pstatement.setInt(2, userID);
			pstatement.setInt(3, estimateID);
			pstatement.executeUpdate();
		}
		return false;
	}
	
	
	/**
	 * Returns a list of all of the estimates of a customer, given the id.
	 * 
	 */
	public List<Estimate> findEstimatesByCustomer(int customerID) throws SQLException {
		List<Estimate> estimates = new ArrayList<>();
		String query = 	"SELECT DISTINCT e.id, e.price " + 
						"FROM estimate AS e, user AS u " + 
						"WHERE e.usrid = ? " + 
						"ORDER BY e.id ASC";
		
		try (PreparedStatement pstatement = connection.prepareStatement(query);) {
			
			pstatement.setInt(1, customerID);
			
			try (ResultSet result = pstatement.executeQuery();) {
				while (result.next()) {
					Estimate estimate = new Estimate();
					
					estimate.setId(result.getInt("id"));
					estimate.setEmployeeId(result.getInt("empid"));
					estimate.setPrice(result.getFloat("price"));
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
					
					estimate.setId(result.getInt("id"));
					estimate.setEmployeeId(result.getInt("empid"));
					estimate.setPrice(result.getFloat("price"));
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

		String query = "SELECT id, empid, price FROM estimate WHERE id = ?";
		try (PreparedStatement pstatement = connection.prepareStatement(query);) {
			pstatement.setInt(1, estimateId);
			try (ResultSet result = pstatement.executeQuery();) {
				if (result.next()) {
					estimate = new Estimate();
					estimate.setId(result.getInt("id"));
					estimate.setEmployeeId(result.getInt("empid"));
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
}