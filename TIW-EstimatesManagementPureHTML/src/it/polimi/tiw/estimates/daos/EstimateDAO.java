package it.polimi.tiw.estimates.daos;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class EstimateDAO {
	private Connection connection;
	private int userID;

	public EstimateDAO(Connection connection,int userID) {
		this.connection = connection;
		this.userID = userID;
	}
	

	public boolean createEstimate(int productID, int[] optionals) throws SQLException  {
		//TODO: THE QUERY
		String query = "INSERT into estimate (usrid, prdid)   VALUES(?, ?)";
		try (PreparedStatement pstatement = connection.prepareStatement(query);) {
			pstatement.setInt(1, userID);
			pstatement.setInt(2, productID);
			pstatement.executeUpdate();
		}
		// LAST_INSERT_ID() will return the last insert id from the current connection
		query = "INSERT into chosenoptional (estid, optid)   VALUES(LAST_INSERT_ID(), ?)";
		try (PreparedStatement pstatement = connection.prepareStatement(query);) {
			pstatement.setInt(1, optionals[0]);
			pstatement.executeUpdate();
		}
		
		return false;
	}
}
