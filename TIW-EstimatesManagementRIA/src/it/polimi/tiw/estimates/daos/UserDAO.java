package it.polimi.tiw.estimates.daos;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import it.polimi.tiw.estimates.beans.Product;
import it.polimi.tiw.estimates.beans.User;

public class UserDAO {
	private Connection connection;

	public UserDAO(Connection connection) {
		this.connection = connection;
	}

	public User checkCredentials(String usr, String psw) throws SQLException {
		String query = "SELECT id, role, username, email, name, surname FROM user WHERE username = ? AND password = ?";
		
		try (PreparedStatement pstatement = connection.prepareStatement(query);) {
			
			pstatement.setString(1, usr);
			pstatement.setString(2, psw);
			
			try (ResultSet result = pstatement.executeQuery();) {
				if (!result.isBeforeFirst()) { // No data, failed to login.
					return null;
				} else {
					result.next();
					User user = new User();
					user.setId(result.getInt("id"));
					user.setRole(result.getString("role"));
					user.setUsername(result.getString("username"));
					user.setEmail(result.getString("email"));
					user.setName(result.getString("name"));
					user.setSurname(result.getString("surname"));
					return user;
				}
			}
		}
	}

	public User findUserById(int userId) throws SQLException {
		User user = null;
		
		String query = "SELECT id, username, email, name, surname FROM user WHERE id = ?";
		
		try (PreparedStatement pstatement = connection.prepareStatement(query);) {
			
			pstatement.setInt(1, userId);
			try (ResultSet result = pstatement.executeQuery();) {	
				
				if (result.next()) {
					user = new User();
					user.setId(result.getInt("id"));
					user.setUsername(result.getString("username"));
					user.setName(result.getString("name"));
					user.setSurname(result.getString("surname"));
					user.setEmail(result.getString("email"));
				}
			}			
		}
		
		return user;
	}
	
	public User findCustomerByEstimate(int estimateId) throws SQLException {
		User user = null;
		
		String query = "SELECT u.id, u.username, u.email, u.name, u.surname "
				+ "FROM user AS u, estimate AS e "
				+ "WHERE u.id = e.usrid "
				+ "AND e.id = ?";
				
		try (PreparedStatement pstatement = connection.prepareStatement(query);) {
			
			pstatement.setInt(1, estimateId);
			
			try (ResultSet result = pstatement.executeQuery();) {
				
				if (result.next()) {
					user = new User();
					user.setId(result.getInt("id"));
					user.setUsername(result.getString("username"));
					user.setName(result.getString("name"));
					user.setSurname(result.getString("surname"));
					user.setEmail(result.getString("email"));
				}	
			}
		}
		return user;
	}
	
	private boolean userExists(String username, String email) throws SQLException {
		String query = "SELECT id, username, email, name, surname FROM user WHERE username = ? AND email = ?";
		
		try (PreparedStatement pstatement = connection.prepareStatement(query);) {
			
			pstatement.setString(1, username);
			pstatement.setString(2, email);

			try (ResultSet result = pstatement.executeQuery();) {	
				if (result == null) return false;
			}			
		}
		
		return true;
	}
	
	public boolean addUser(String username, String email, String password, String name, String surname ) throws SQLException {
		String query =	"INSERT INTO `dbgroup2`.`user` (`username`, `password`, `email`, `name`, `surname`) "+
						" VALUES (?, ?, ?, ?, ?)";
		
		if(!userExists(username,email)) {
			try (PreparedStatement pstatement = connection.prepareStatement(query);) {
				pstatement.setString(1, username);
				pstatement.setString(2, password);
				pstatement.setString(3, email);
				pstatement.setString(4, name);
				pstatement.setString(5, surname);		
			}
			return true;
		}
		return false;
	}

}
