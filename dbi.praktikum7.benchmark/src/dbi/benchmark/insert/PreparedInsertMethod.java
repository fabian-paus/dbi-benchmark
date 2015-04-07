package dbi.benchmark.insert;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Random;

/**
 * Diese Klasse implementiert das Einfügen von Daten über PreparedStatements.
 * Dabei werden INSERT Befehle verwendet.
 * 
 */
public class PreparedInsertMethod implements InsertMethod {
	private Random random;

	/**
	 * Erstellt eine neue Instanz der Klasse.
	 */
	public PreparedInsertMethod() {
		random = new Random();
	}

	/**
	 * Fügt Daten über PreparedStatements in die Datenbank ein.
	 */
	@Override
	public void doInsert(int scaleN, Connection connection) throws SQLException {

		insertBranches(connection, scaleN);
		insertAccounts(connection, scaleN);
		insertTellers(connection, scaleN);
	}

	/**
	 * Füllt die Tabelle 'tellers'.
	 * 
	 * @param connection Datenbankverbindung.
	 * @param scaleN Skalierungsfaktor n.
	 * @throws SQLException Bei Zugriffsfehler auf die Datenbank.
	 */
	private void insertTellers(Connection connection, int scaleN)
			throws SQLException {
		final String sql = "INSERT INTO tellers "
				+ "(tellerid, tellername, balance, branchid, address) "
				+ "VALUES ( ?, ?, ?, ?, ? )";

		PreparedStatement statement = null;

		try {
			statement = connection.prepareStatement(sql);

			int tellerCount = scaleN * FixedData.RATIO_TELLERS_TO_BRANCHES;
			for (int tellerID = 1; tellerID <= tellerCount; ++tellerID) {
				statement.setInt(1, tellerID);
				statement.setString(2, FixedData.TELLER_NAME);
				statement.setInt(3, FixedData.TELLER_BALANCE);
				int branchID = generateBranchID(scaleN);
				statement.setInt(4, branchID);
				statement.setString(5, FixedData.TELLER_ADDRESS);

				statement.executeUpdate();
			}
		} finally {
			if (statement != null)
				statement.close();
		}
	}

	/**
	 * Füllt die Tabelle 'accounts'.
	 * 
	 * @param connection Datenbankverbindung.
	 * @param scaleN Skalierungsfaktor n.
	 * @throws SQLException Bei Zugriffsfehler auf die Datenbank.
	 */
	private void insertAccounts(Connection connection, int scaleN)
			throws SQLException {
		final String sql = "INSERT INTO accounts " +
				"(accid, name, balance, branchid, address) "
				+ "VALUES ( ?, ?, ?, ?, ? )";

		PreparedStatement stmt = null;

		try {
			stmt = connection.prepareStatement(sql);

			int accountsCount = scaleN * FixedData.RATIO_ACCOUNTS_TO_BRANCHES;
			for (int accountID = 1; accountID <= accountsCount; ++accountID) {

				stmt.setInt(1, accountID);
				stmt.setString(2, FixedData.ACCOUNT_NAME);
				stmt.setInt(3, FixedData.ACCOUNT_BALANCE);
				int branchID = generateBranchID(scaleN);
				stmt.setInt(4, branchID);
				stmt.setString(5, FixedData.ACCOUNT_ADDRESS);

				stmt.executeUpdate();
			}
		} finally {
			if (stmt != null)
				stmt.close();
		}
	}

	/**
	 * Füllt die Tabelle 'branches'.
	 * 
	 * @param connection Datenbankverbindung.
	 * @param scaleN Skalierungsfaktor n.
	 * @throws SQLException Bei Zugriffsfehler auf die Datenbank.
	 */
	private void insertBranches(Connection connection, int scaleN)
			throws SQLException {
		final String sql = "INSERT INTO branches " +
				"(branchid, branchname, balance, address) "
				+ "VALUES ( ?, ?, ?, ? )";

		PreparedStatement statement = null;
		try {
			statement = connection.prepareStatement(sql);

			int branchCount = scaleN;
			for (int branchID = 1; branchID <= branchCount; ++branchID) {
				statement.setInt(1, branchID);
				statement.setString(2, FixedData.BRANCH_NAME);
				statement.setInt(3, FixedData.BRANCH_BALANCE);
				statement.setString(4, FixedData.BRANCH_ADDRESS);

				statement.executeUpdate();
			}
		} finally {
			if (statement != null)
				statement.close();
		}
	}

	/**
	 * Erzeugt eine zufällige Branch-ID.
	 * 
	 * @param scaleN Skalierungsfaktor n.
	 * @return Zufällige Branch-ID.
	 */
	private int generateBranchID(int scaleN) {
		return random.nextInt(scaleN) + 1;
	}
}
