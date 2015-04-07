package my.benchmark;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Diese Klasse implementiert alle drei Lasttransaktion über
 * Prepared Statements.
 */
public class PreparedStatementTransactions implements
		AccountBalanceTransaction, DepositMoneyTransaction,
		CountDeltaTransaction {
	
	private Connection connection;
	
	private PreparedStatement selectAccountBalance;
	private PreparedStatement updateAccountBalance;
	private PreparedStatement updateTellerBalance;
	private PreparedStatement updateBranchBalance;
	private PreparedStatement insertHistoryEntry;
	private PreparedStatement countHistoryEntries;
	
	
	private static final String SQL_SELECT_ACCOUNT_BALANCE =
			"SELECT balance FROM accounts WHERE accid = ?";
	
	private static final String SQL_UPDATE_ACCOUNT_BALANCE =
			"UPDATE accounts SET balance = balance + ? " +
			" WHERE accid = ?";
	
	private static final String SQL_UPDATE_TELLER_BALANCE =
			"UPDATE tellers SET balance = balance + ? " +
			" WHERE tellerid = ?";
	
	private static final String SQL_UPDATE_BRANCH_BALANCE =
			"UPDATE branches SET balance = balance + ? " +
			" WHERE branchid = ?";
	
	private static final String SQL_INSERT_HISTORY_ENTRY =
			"INSERT INTO history " +
			"(accid, tellerid, delta, branchid, accbalance, cmmnt) " +
			"VALUES (?, ?, ?, ?, ?, ?)";
	
	private static final String SQL_COUNT_HISTORY_ENTRIES =
			"SELECT COUNT(*) AS entrycount FROM history WHERE delta = ?";
	
	/**
	 * Initialisert die verwendeten Prepared Statements.
	 */
	@Override
	public void initialize(Connection connection) throws SQLException {
		this.connection = connection;
		
		selectAccountBalance = prepare(SQL_SELECT_ACCOUNT_BALANCE);
		updateAccountBalance = prepare(SQL_UPDATE_ACCOUNT_BALANCE);
		updateTellerBalance = prepare(SQL_UPDATE_TELLER_BALANCE);
		updateBranchBalance = prepare(SQL_UPDATE_BRANCH_BALANCE);
		insertHistoryEntry = prepare(SQL_INSERT_HISTORY_ENTRY);
		countHistoryEntries = prepare(SQL_COUNT_HISTORY_ENTRIES);
	}

	/**
	 * Schließt alle verwendeten Prepared Statements.
	 */
	@Override
	public void close() throws SQLException {
		if (countHistoryEntries != null)
			countHistoryEntries.close();
		if (insertHistoryEntry != null)
			insertHistoryEntry.close();
		if (updateBranchBalance != null)
			updateBranchBalance.close();
		if (updateTellerBalance != null)
			updateTellerBalance.close();
		if (updateAccountBalance != null)
			updateAccountBalance.close();
		if (selectAccountBalance != null)
			selectAccountBalance.close();
	}
	
	/**
	 * Fragt den Kontostand über ein Prepared Statement ab.
	 */
	@Override
	public int getAccountBalance(int accountID) throws SQLException {
		ResultSet result = null;
		try {
			selectAccountBalance.setInt(1, accountID);
			result = selectAccountBalance.executeQuery();

			// Es gibt nur ein Ergebnis, da accid Primärschlüssel ist
			result.next();

			return result.getInt("balance");
		} finally {
			if (result != null)
				result.close();
		}
	}
	
	/**
	 * Führt einen Einzahlungsvorgang mit mehreren Prepared Statements aus.
	 */
	@Override
	public int depositMoney(int accountID, int tellerID, int branchID, int delta)
			throws SQLException {
		
		updateBranchBalance.setInt(1, delta);
		updateBranchBalance.setInt(2, branchID);
		updateBranchBalance.executeUpdate();
		
		updateTellerBalance.setInt(1, delta);
		updateTellerBalance.setInt(2, tellerID);
		updateTellerBalance.executeUpdate();
		
		updateAccountBalance.setInt(1, delta);
		updateAccountBalance.setInt(2, accountID);
		updateAccountBalance.executeUpdate();
		
		// Den aktualisierten Kontostand in der History-Tabelle eintragen
		int newBalance = getAccountBalance(accountID);
		
		insertHistoryEntry.setInt(1, accountID);
		insertHistoryEntry.setInt(2, tellerID);
		insertHistoryEntry.setInt(3, delta);
		insertHistoryEntry.setInt(4, branchID);
		insertHistoryEntry.setInt(5, newBalance);
		insertHistoryEntry.setString(6, FixedData.HISTORY_COMMENT);
		insertHistoryEntry.executeUpdate();
		
		return newBalance;
	}

	/**
	 * Zählt History-Einträge mit einem bestimmten Einzahlungsbetrag
	 * durch ein Prepared Statement.
	 */
	@Override
	public int countDeltaEntries(int delta) throws SQLException {
		ResultSet result = null;
		try {
			countHistoryEntries.setInt(1, delta);
			result = countHistoryEntries.executeQuery();

			// Es gibt nur ein Ergebnis, da COUNT(*) zurückgegeben wird
			result.next();

			return result.getInt("entrycount");
		} finally {
			if (result != null)
				result.close();
		}
	}
	
	/**
	 * Initialisiert ein Prepared Statement.
	 * 
	 * @param sql SQL-Befehl.
	 * @return Prepared Statement.
	 * @throws SQLException Bei einem Zugriffsfehler auf die Datenbank.
	 */
	private PreparedStatement prepare(String sql) throws SQLException {
		return connection.prepareStatement(sql);
	}	
}
