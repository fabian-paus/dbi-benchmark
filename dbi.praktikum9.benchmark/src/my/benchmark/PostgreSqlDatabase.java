package my.benchmark;

import java.sql.Statement;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Random;

import dbi.benchmark.insert.FixedData;
import de.whs.dbi.loaddriver.Database;
import de.whs.dbi.util.Configuration;

/**
 * Diese Klasse stellt den Einstieg in das DBI Benchmark Framework dar.
 * Die Methoden zum Öffnen und Schließen der Datenbank werden für
 * die PostgreSQL-Datenbank angepasst. Außerdem werden die Methoden
 * für die Ausführung der Lasttransaktionen bereitgestellt.
 *
 */
public class PostgreSqlDatabase extends Database {
	private Random random;
	private int scaleN;
	private AccountBalanceTransaction accountBalanceTX;
	private DepositMoneyTransaction depositMoneyTX;
	private CountDeltaTransaction countDeltaTX;

	/**
	 * Initialisiert die Zufallszahlengenerierung für die Lasttransaktionen
	 * und lädt die konkreten Implementierungen der Transaktionen.
	 * 
	 * @param config Konfigurationsobjekt.
	 * @throws Exception Bei einem Konfigurationsfehler.
	 */
	public PostgreSqlDatabase(Configuration config) throws Exception {
		super(config);
		
		random = new Random();
		scaleN = Integer.parseInt(config.getUser("n"));
		loadTransactions(config);
	}

	/**
	 * Öffnet eine Datenbankverbindung mit Benutzername und -passwort aus
	 * der Konfigurationsdatei. Die Implementierung in Database hat die
	 * Verbindung ohne Authentifizierung aufgebaut.
	 */
	@Override
	protected void openConnection() throws SQLException {
		String url = config.getDatabaseJDBC();
		String username = config.getUser("database.username");
		String password = config.getUser("database.password");
		connection = DriverManager.getConnection(url, username, password);
		
		// Default bei PostgreSQL ist Read Committed, für unsere TX wird 
		// SERIALIZABLE gefordert
		connection.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);
		
		clearHistory();
	}
	
	/**
	 * Gibt alle Ressourcen der Implementierungen der Lasttransaktionen frei
	 * und schließt danach die Datenbankverbindung.
	 */
	@Override
	public void closeConnection() throws SQLException {
		if (accountBalanceTX != null)
			accountBalanceTX.close();
		if (depositMoneyTX != null)
			depositMoneyTX.close();
		if (countDeltaTX != null)
			countDeltaTX.close();
		
		super.closeConnection();
	}

	/**
	 * Leert die History-Tabelle.
	 * 
	 * @throws SQLException Bei einem Zugriffsfehler auf die Datenbank.
	 */
	private void clearHistory() throws SQLException {
		Statement statement = connection.createStatement();
		try {
			statement.executeUpdate("TRUNCATE history");
		}
		finally {
			statement.close();
		}
	}

	/**
	 * Diese Lasttransaktion fragt für ein zufällig gewähltes
	 * Konto den Kontostand ab.
	 * 
	 * @throws SQLException Bei einem Zugriffsfehler auf die Datenbank.
	 */
	public void kontostandsTX() throws SQLException {
		int accountID = generateAccountID();
		
		// Rückgabewert wird im Benchmark nicht verwendet
		@SuppressWarnings("unused")
		int balance = accountBalanceTX.getAccountBalance(accountID);
	}

	/**
	 * Diese Transaktion zahlt einen bestimmten Betrag auf ein
	 * zufällig gewähltes Konto ein. Dabei werden ein Branch und
	 * ein Teller ebenfalls zufällig gewählt und aktualisert. 
	 * Am Ende wird ein Eintrag in der History-Tabelle angelegt.
	 * 
	 * @throws SQLException Bei einem Zugriffsfehler auf die Datenbank.
	 */
	public void einzahlungsTX() throws SQLException {
		int accountID = generateAccountID();
		int tellerID = generateTellerID();
		int branchID = generateBranchID();
		int delta = generateDelta();
		
		// Rückgabewert wird im Benchmark nicht verwendet
		@SuppressWarnings("unused")
		int newBalance = depositMoneyTX.depositMoney(accountID, tellerID,
				branchID, delta);
	}
	
	/**
	 * Diese Transaktion zählt history-Einträge mit einem
	 * bestimmten Einzahlungsbetrag.
	 * 
	 * @throws SQLException Bei einem Zugriffsfehler auf die Datenbank.
	 */
	public void analyseTX() throws SQLException {
		int delta = generateDelta();
		
		// Rückgabewert wird im Benchmark nicht verwendet
		@SuppressWarnings("unused")
		int count = countDeltaTX.countDeltaEntries(delta);
	}
	
	/**
	 * Lädt die konkreten Implementierungen der Lasttransaktionen auf Basis
	 * eines Konfigurationsobjektes.
	 * 
	 * @param config Konfigurationsobjekt.
	 * @throws Exception Bei einem Konfigurationsfehler.
	 */
	private void loadTransactions(Configuration config) throws Exception {
		accountBalanceTX = (AccountBalanceTransaction) 
				loadTransaction("tx.account-balance");
		depositMoneyTX = (DepositMoneyTransaction) 
				loadTransaction("tx.deposit-money");
		countDeltaTX = (CountDeltaTransaction) 
				loadTransaction("tx.count-delta");
	}
	
	/**
	 * Lädt eine konkrete Implementierung einer Lasttransaktion und
	 * initialisiert diese.
	 * 
	 * @param paramName Klassenname der konkreten Implementierung.
	 * @return Lasttransaktion.
	 * @throws Exception Falls die Klasse nicht geladen werden konnte.
	 */
	private Transaction loadTransaction(String paramName) throws Exception {
		// Klasse über Reflection laden
		String className = config.getUser(paramName);
		Class<?> class_ = Class.forName(className);
		
		// Objekt der Klasse instantieren und initialisieren
		Transaction transaction = (Transaction)class_.newInstance();
		transaction.initialize(connection);
		
		return transaction;
	}
	
	/**
	 * Generiert eine zufällige ID für Branches.
	 * 
	 * @return Zufällige ID.
	 */
	private int generateBranchID() {
		int branchCount = scaleN;
		return random.nextInt(branchCount) + 1;
	}
	
	/**
	 * Generiert eine zufällige ID für Tellers.
	 * 
	 * @return Zufällige ID.
	 */
	private int generateTellerID() {
		int tellerCount = scaleN * FixedData.RATIO_TELLERS_TO_BRANCHES;
		return random.nextInt(tellerCount) + 1;
	}
	
	/**
	 * Generiert eine zufällige ID für Accounts.
	 * 
	 * @return Zufällige ID.
	 */
	private int generateAccountID() {
		int accountCount = scaleN * FixedData.RATIO_ACCOUNTS_TO_BRANCHES;
		return random.nextInt(accountCount) + 1;
	}
	
	/**
	 * Generiert einen zufälligen Einzahlungsbetrag.
	 * 
	 * @return Zufälliger Einzahlungsbetrag.
	 */
	private int generateDelta() {
		final int MAX_DELTA = 10000;
		return random.nextInt(MAX_DELTA) + 1;
	}
}
