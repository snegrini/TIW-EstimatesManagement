package it.polimi.tiw.estimates.daos;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import it.polimi.tiw.estimates.beans.Optional;
import it.polimi.tiw.estimates.beans.OptionalType;

public class OptionalDAO {
	
	private Connection connection;

	public OptionalDAO(Connection connection) {
		this.connection = connection;
	}
	
	public List<Optional> findAvailableOptionalsByProduct(int productId) throws SQLException {
		List<Optional> optionals = new ArrayList<>();
		
		// Only products with at least one optional
		String query = "SELECT o.id, o.name, o.type "
				+ "FROM optional AS o, optionaltoproduct AS otp "
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
	
	public List<Optional> findChosenOptionalsByEstimate(int estimateId) throws SQLException {
		List<Optional> optionals = new ArrayList<>();
		
		// Only products with at least one optional
		String query = "SELECT o.id, o.name, o.type "
				+ "FROM optional AS o, chosenoptional AS cho "
				+ "WHERE o.id = cho.optid "
				+ "AND cho.estid = ?";
				
		try (PreparedStatement pstatement = connection.prepareStatement(query);) {
			
			pstatement.setInt(1, estimateId);
			
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
