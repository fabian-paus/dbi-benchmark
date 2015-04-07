package dbi.benchmark.insert;

import java.sql.Statement;
import java.sql.Connection;
import java.sql.SQLException;

/**
 * Diese Klasse implementiert das Decorator-Pattern. Dabei wird eine bestehende
 * InsertMethod um das Entfernen von Foreign-Key-Constraints vor dem Einfügen
 * und das Hinzufügen dieser Constraints nach dem Einfügen erweitert.
 * 
 */
public class ConstraintStripMethod implements InsertMethod {
	private final String DROP_ACCOUNTS_FOREIGN_KEY = 
			"ALTER TABLE accounts DROP CONSTRAINT accounts_branchid_fkey";
	private final String DROP_TELLERS_FOREIGN_KEY = 
			"ALTER TABLE tellers DROP CONSTRAINT tellers_branchid_fkey";
	
	private final String ADD_ACCOUNTS_FOREIGN_KEY = 
			"ALTER TABLE accounts ADD CONSTRAINT accounts_branchid_fkey " +
			"FOREIGN KEY (branchid) REFERENCES branches";
	private final String ADD_TELLERS_FOREIGN_KEY = 
			"ALTER TABLE tellers ADD CONSTRAINT tellers_branchid_fkey " +
			"FOREIGN KEY (branchid) REFERENCES branches";
	
	private InsertMethod decoratedMethod;
	
	/**
	 * Erweitert eine InsertMethod um das Entfernen und Hinzufügen von
	 * Foreign-Key-Constraints.
	 * 
	 * @param method Methode, die erweitert werden soll.
	 */
	public ConstraintStripMethod(InsertMethod method) {
		decoratedMethod = method;
	}

	/**
	 * Vor dem Ausführen der Basis-Methode werden die Foreign-Key-Constraints
	 * entfernt. Nach der Ausführung werden die Constraints wieder hinzugefügt.
	 */
	@Override
	public void doInsert(int scaleN, Connection connection) throws SQLException {
		Statement statement = null;
		
		try {
			statement = connection.createStatement();

			dropContraints(statement);
			decoratedMethod.doInsert(scaleN, connection);
			addConstraints(statement);
		}
		finally {
			if (statement != null)
				statement.close();
		}
	}

	/**
	 * Entfernt alle Foreign-Key-Constraints aus den Tabellen.
	 * 
	 * @param statement Statement, das zur Ausführung verwendet wird.
	 * @throws SQLException Bei Zugriffsfehler auf die Datenbank.
	 */
	private void dropContraints(Statement statement) throws SQLException {
		statement.executeUpdate(DROP_ACCOUNTS_FOREIGN_KEY);
		statement.executeUpdate(DROP_TELLERS_FOREIGN_KEY);
	}

	/**
	 * Fügt alle Foreign-Key-Constraints den Tabellen hinzu.
	 * 
	 * @param statement Statement, das zur Ausführung verwendet wird.
	 * @throws SQLException Bei Zugriffsfehler auf die Datenbank.
	 */
	private void addConstraints(Statement statement) throws SQLException {
		statement.executeUpdate(ADD_ACCOUNTS_FOREIGN_KEY);
		statement.executeUpdate(ADD_TELLERS_FOREIGN_KEY);
	}
}
