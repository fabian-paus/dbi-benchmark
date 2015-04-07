package dbi.benchmark;

import java.sql.*;
import java.io.IOException;

import dbi.benchmark.insert.InsertMethod;

/**
 * Diese Klasse führt einen Benchmark für die PostgreSQL Datenbank durch.
 * Dabei werden Verbindungsdaten und weitere Einstellungen aus einer zentralen
 * Konfiguration gelesen. Nach Durchführung des Benchmarks kann die benötigte 
 * Zeit für die Operation abgefragt werden.
 * 
 * Zur Ausführung der Methode zum Einfügen der Daten verwendet diese Klasse das
 * Strategy-Pattern. Die konkrete Einfügestrategie wird durch Implementierungen
 * von InsertMethod bestimmt. Die in der Konfiguration gewählte Methode wird
 * durch eine Factory erzeugt.
 *
 */
public class Benchmarker {
	private Connection connection;
	private Statement statement;
	private int scaleN;
	private InsertMethod method;
	private long startTime;
	private long endTime;

	/**
	 * Erstellt eine neue Benchmarker Instanz mit der gegebenen Konfiguration.
	 * 
	 * @param config Konfiguration.
	 * @throws SQLException Wenn keine Verbindung zur Datenbank aufgebaut
	 * 		   				werden konnte.
	 * @throws IOException Wenn die Konfiguration fehlerhaft ist.
	 */
	public Benchmarker(Configuration config) throws SQLException, IOException {
		String url = config.getJdbcUrl();
		String user = config.getJdbcUser();
		String password = config.getJdbcPassword();
		int methodID = config.getInsertMethod();

		connection = DriverManager.getConnection(url, user, password);
		statement = connection.createStatement();
		scaleN = config.getScaleN();
		method = InsertMethodFactory.create(methodID);
	}

	/**
	 * Führt den Benchmark aus.
	 * 
	 * @throws SQLException Bei einem Zugriffsfehler auf die Datenbank.
	 */
	public void execute() throws SQLException {
		createTables();
		startTimer();

		method.doInsert(scaleN, connection);

		endTimer();
	}

	/**
	 * Bestimmt die benötigte Zeit für die gemessene Operation.
	 * Diese Methode liefert erst nach dem Aufruf von execute gültige Werte.
	 * 
	 * @return Benötigte Zeit des Benchmarks.
	 */
	public long getElapsedTime() {
		return endTime - startTime;
	}

	/**
	 * Gibt alle verwendeten Ressourcen wieder frei.
	 * 
	 * @throws SQLException Bei einem Zugriffsfehler auf die Datenbank.
	 */
	public void close() throws SQLException {
		if (statement != null)
			statement.close();
		if (connection != null)
			connection.close();
	}	

	/**
	 * Startet den Timer für den Benchmark.
	 */
	private void startTimer() {
		startTime = System.currentTimeMillis();
	}

	/**
	 * Beendet den Timer für den Benchmark.
	 */
	private void endTimer() {
		endTime = System.currentTimeMillis();
	}

	/**
	 * Erstellt alle verwendeten Tabellen der Datenbank.
	 * 
	 * @throws SQLException Bei einem Zugriffsfehler auf die Datenbank.
	 */
	private void createTables() throws SQLException {
		ensureTablesAreDropped();

		statement.executeUpdate(TableStatements.CREATE_BRANCHES);
		statement.executeUpdate(TableStatements.CREATE_ACCOUNTS);
		statement.executeUpdate(TableStatements.CREATE_TELLERS);
		statement.executeUpdate(TableStatements.CREATE_HISTORY);
	}
	
	/**
	 * Löscht alle verwendeten Tabellen aus der Datenbank.
	 * 
	 * @throws SQLException Bei einem Zugriffsfehler auf die Datenbank.
	 */
	private void dropTables() throws SQLException {
		statement.executeUpdate(TableStatements.DROP_TABLES);
	}

	/**
	 * Stellt sicher, dass die Tabellen nicht in der Datenbank vorhanden sind.
	 * Das ist notwendig, damit es beim Erstellen der Tabellen keine Fehler
	 * auftreten.
	 */
	private void ensureTablesAreDropped() {
		try {
			dropTables();
		} catch (SQLException ex) {
			// Die Ausnahme kann ignoriert werden, da im Fehlerfall die
			// Tabellen nicht gelöscht werden konnten, weil sie nicht
			// existieren
		}
	}
}
