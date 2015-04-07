package my.benchmark;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;


/**
 * Diese Klasse implementiert die Einzahlungstransaktion über den Aufruf einer
 * Stored Procedure. Diese muss vorher im DBMS angelegt worden sein.
 * 
 */
public class StoredProcedureTransactions implements DepositMoneyTransaction {

	private PreparedStatement callDepositMoney;
	
	/**
	 * Initialisiert den Aufruf der Stored Procedure.
	 */
	@Override
	public void initialize(Connection connection) throws SQLException {
		String sql = "SELECT depositmoney(?, ?, ?, ?, ?)";
		callDepositMoney = connection.prepareStatement(sql);
	}

	/**
	 * Gibt alle verwendeten Ressourcen frei.
	 */
	@Override
	public void close() throws SQLException {
		if (callDepositMoney != null)
			callDepositMoney.close();
	}

	/**
	 * Führt die Einzahlungstransaktion über den Aufruf einer
	 * Stored Procedure aus.
	 */
	@Override
	public int depositMoney(int accountID, int tellerID, int branchID, int delta)
			throws SQLException {
		
		callDepositMoney.setInt(1, accountID);
		callDepositMoney.setInt(2, tellerID);
		callDepositMoney.setInt(3, branchID);
		callDepositMoney.setInt(4, delta);
		callDepositMoney.setString(5, FixedData.HISTORY_COMMENT);
		
		ResultSet result = callDepositMoney.executeQuery();
		
		try {
			// Stored Procedure liefert den aktualisierten Kontostand
			result.next();
			return result.getInt(1);
		}
		finally {			
			result.close();
		}
	}
}
