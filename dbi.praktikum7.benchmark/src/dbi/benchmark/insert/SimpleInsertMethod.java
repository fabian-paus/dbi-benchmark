package dbi.benchmark.insert;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Random;

/**
 * Diese Klasse implementiert das Einf�gen von Daten �ber einfache Statements.
 * Dabei werden INSERT Befehle verwendet.
 *
 */
public class SimpleInsertMethod implements InsertMethod {
	private Random random;
	
	/**
	 * Erstellt eine neue Instanz der Klasse.
	 */
	public SimpleInsertMethod() {
		random = new Random();		
	}

	/**
	 * F�gt Daten �ber einfache Statements in die Datenbank ein.
	 */
	@Override
	public void doInsert(int scaleN, Connection connection) throws SQLException {
		Statement statement = null;
		
		try {
			statement = connection.createStatement();
			
			insertBranches(statement, scaleN);
			insertAccounts(statement, scaleN);
			insertTellers(statement, scaleN);
		}
		finally {
			statement.close();
		}
	}

	/**
	 * F�llt die Tabelle 'tellers'.
	 * 
	 * @param statement �ber das Statement werden die Befehle abgesetzt.
	 * @param scaleN Skalierungsfaktor n.
	 * @throws SQLException Bei Zugriffsfehler auf die Datenbank.
	 */
	private void insertTellers(Statement statement, int scaleN)
			throws SQLException {
		final String insertBegin = "INSERT INTO tellers "
				+ "(tellerid, tellername, balance, branchid, address) "
				+ "VALUES (";

		for (int id = 1; id <= scaleN * FixedData.RATIO_TELLERS_TO_BRANCHES; ++id) {
			int branchid = generateBranchID(scaleN);
			String sql = insertBegin + id + ", '" + FixedData.TELLER_NAME
					+ "'," + FixedData.TELLER_BALANCE + ", " + branchid + ", '"
					+ FixedData.TELLER_ADDRESS + "')";
			
			statement.executeUpdate(sql);
		}
	}

	/**
	 * F�llt die Tabelle 'accounts'.
	 * 
	 * @param statement �ber das Statement werden die Befehle abgesetzt.
	 * @param scaleN Skalierungsfaktor n.
	 * @throws SQLException Bei Zugriffsfehler auf die Datenbank.
	 */
	private void insertAccounts(Statement statement, int scaleN) throws SQLException {
		final String insertBegin = "INSERT INTO accounts "
				+ "(accid, name, balance, branchid, address) " 
				+ "VALUES (";

		for (int id = 1; id <= scaleN * FixedData.RATIO_ACCOUNTS_TO_BRANCHES; ++id) {
			int branchid = generateBranchID(scaleN);
			String sql = insertBegin + id + ", '" + FixedData.ACCOUNT_NAME
					+ "'," + FixedData.ACCOUNT_BALANCE + ", " + branchid
					+ ", '" + FixedData.ACCOUNT_ADDRESS + "')";
			
			statement.executeUpdate(sql);
		}
	}

	/**
	 * F�llt die Tabelle 'branches'.
	 * 
	 * @param statement �ber das Statement werden die Befehle abgesetzt.
	 * @param scaleN Skalierungsfaktor n.
	 * @throws SQLException Bei Zugriffsfehler auf die Datenbank.
	 */
	private void insertBranches(Statement statement, int scaleN)
			throws SQLException {
		final String insertBegin = "INSERT INTO branches " +
				"(branchid, branchname, balance, address) "
				+ "VALUES (";

		for (int id = 1; id <= scaleN; ++id) {
			String sql = insertBegin + id + ", '" + FixedData.BRANCH_NAME
					+ "', " + FixedData.BRANCH_BALANCE + ", '"
					+ FixedData.BRANCH_ADDRESS + "')";
			
			statement.executeUpdate(sql);
		}
	}

	/**
	 * Erzeugt eine zuf�llige Branch-ID.
	 * 
	 * @param scaleN Skalierungsfaktor n.
	 * @return Zuf�llige Branch-ID.
	 */
	private int generateBranchID(int scaleN) {
		return random.nextInt(scaleN) + 1;
	}
}
