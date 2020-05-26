package it.polimi.tiw.estimates.daos;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import it.polimi.tiw.estimates.beans.User;

public class UserDAO {
	private Connection connection;

	public UserDAO(Connection connection) {
		this.connection = connection;
	}

	public User checkCredentials(String usr, String psw) throws SQLException {
		String query = "SELECT  id, role, username FROM user WHERE username = ? AND password = ?";
		
		try (PreparedStatement pstatement = connection.prepareStatement(query);) {
			
			pstatement.setString(1, usr);
			pstatement.setString(2, psw);
			
			try (ResultSet result = pstatement.executeQuery();) {
				if (!result.isBeforeFirst()) { // No results, credential check failed.
					return null;
				} else {
					result.next();
					User user = new User();
					user.setId(result.getInt("id"));
					user.setRole(result.getString("role"));
					user.setUsername(result.getString("username"));
					return user;
				}
			}
		}
	}
}