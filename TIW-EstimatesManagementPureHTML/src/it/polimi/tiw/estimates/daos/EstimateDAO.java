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

	public EstimateDAO(Connection connection) {
		this.connection = connection;
	}
	

	public boolean createEstimate(int customerId, int productId, String[] optionalsId) throws SQLException  {
		String query = "INSERT into estimate (usrid, prdid) VALUES(?, ?)";
		
		try (PreparedStatement pstatement = connection.prepareStatement(query);) {
			pstatement.setInt(1, customerId);
			pstatement.setInt(2, productId);
			pstatement.executeUpdate();
		}
		
		// LAST_INSERT_ID() will return the last insert id from the current connection
		for(int i = 0; i < optionalsId.length; i++) {
			query = "INSERT into chosenoptional (estid, optid) VALUES(LAST_INSERT_ID(), ?)";
			try (PreparedStatement pstatement = connection.prepareStatement(query);) {
				pstatement.setInt(1, Integer.parseInt(optionalsId[i]));
				pstatement.executeUpdate();
			}
		}
		
		return false;
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
	

	public Estimate findDefaultEstimateByCustomer(int customerId) throws SQLException {
		
		Estimate estimate = null;
		String query = "SELECT id, prdid, empid, price " +
				"FROM estimate " +
				"WHERE usrid = ? " +
				"ORDER BY id ASC LIMIT 1";
		
		try (PreparedStatement pstatement = connection.prepareStatement(query);) {
			pstatement.setInt(1, customerId);
			
			try (ResultSet result = pstatement.executeQuery();) {	
				if (result.next()) {
					estimate = new Estimate();
					estimate.setId(result.getInt("id"));
					estimate.setProductId(result.getInt("prdid"));
					estimate.setEmployeeId(result.getInt("empid"));
					estimate.setPrice(result.getFloat("price"));
				}	
			}	
		}	
		return estimate;
	}
	
	public Estimate findDefaultEstimateByEmployee(int employeeId) throws SQLException {
		
		Estimate estimate = null;
		String query = "SELECT id, usrid, prdid, price " +
				"FROM estimate " +
				"WHERE empid = ? " +
				"ORDER BY id ASC LIMIT 1";
		
		try (PreparedStatement pstatement = connection.prepareStatement(query);) {
			pstatement.setInt(1, employeeId);
			
			try (ResultSet result = pstatement.executeQuery();) {	
				if (result.next()) {
					estimate = new Estimate();
					estimate.setId(result.getInt("id"));
					estimate.setProductId(result.getInt("prdid"));
					estimate.setCustomerId(result.getInt("usrid"));
					estimate.setPrice(result.getFloat("price"));
				}	
			}	
		}	
		return estimate;
	}
	
	
	/*
	 * return a list of all of the estimates of a customer, given the id.
	 * */
	public List<Estimate> findEstimatesByCustomer(int customerId) throws SQLException {
		List<Estimate> estimates = new ArrayList<>();
		String query = 	"SELECT e.id, e.prdid, e.price " + 
						"FROM estimate AS e " + 
						"WHERE e.usrid = ? " + 
						"ORDER BY e.id ASC";
		
		try (PreparedStatement pstatement = connection.prepareStatement(query);) {
			
			pstatement.setInt(1, customerId);
			
			try (ResultSet result = pstatement.executeQuery();) {
				while (result.next()) {
					Estimate estimate = new Estimate();
					
					estimate.setId(result.getInt("id"));
					estimate.setProductId(result.getInt("prdid"));			
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
					estimate.setCustomerId(result.getInt("usrid"));
					estimate.setProductId(result.getInt("prdid"));			
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
		
		String query = "SELECT id, prdid FROM estimate WHERE empid IS NULL AND price IS NULL";
		
		try (PreparedStatement pstatement = connection.prepareStatement(query);) {
						
			try (ResultSet result = pstatement.executeQuery();) {
				
				while (result.next()) {
					Estimate estimate = new Estimate();
										
					estimate.setId(result.getInt("id"));
					estimate.setProductId(result.getInt("prdid"));		
					estimates.add(estimate);
				}
			}
		}
		return estimates;
	}
	
	public Estimate findEstimateById(int estimateId) throws SQLException {
		Estimate estimate = null;

		String query = "SELECT id, usrid, prdid, empid, price FROM estimate WHERE id = ?";
		try (PreparedStatement pstatement = connection.prepareStatement(query);) {
			pstatement.setInt(1, estimateId);
			try (ResultSet result = pstatement.executeQuery();) {
				if (result.next()) {
					estimate = new Estimate();
					estimate.setId(result.getInt("id"));
					estimate.setCustomerId(result.getInt("usrid"));
					estimate.setProductId(result.getInt("prdid"));
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
