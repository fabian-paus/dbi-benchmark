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
 * Die Methoden zum �ffnen und Schlie�en der Datenbank werden f�r
 * die PostgreSQL-Datenbank angepasst. Au�erdem werden die Methoden
 * f�r die Ausf�hrung der Lasttransaktionen bereitgestellt.
 *
 */
public class PostgreSqlDatabase extends Database {
	private Random random;
	private int scaleN;
	private AccountBalanceTransaction accountBalanceTX;
	private DepositMoneyTransaction depositMoneyTX;
	private CountDeltaTransaction countDeltaTX;

	/**
	 * Initialisiert die Zufallszahlengenerierung f�r die Lasttransaktionen
	 * und l�dt die konkreten Implementierungen der Transaktionen.
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
	 * �ffnet eine Datenbankverbindung mit Benutzername und -passwort aus
	 * der Konfigurationsdatei. Die Implementierung in Database hat die
	 * Verbindung ohne Authentifizierung aufgebaut.
	 */
	@Override
	protected void openConnection() throws SQLException {
		String url = config.getDatabaseJDBC();
		String username = config.getUser("database.username");
		String password = config.getUser("database.password");
		connection = DriverManager.getConnection(url, username, password);
		
		// Default bei PostgreSQL ist Read Committed, f�r unsere TX wird 
		// SERIALIZABLE gefordert
		connection.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);
		
		clearHistory();
	}
	
	/**
	 * Gibt alle Ressourcen der Implementierungen der Lasttransaktionen frei
	 * und schlie�t danach die Datenbankverbindung.
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
	 * Diese Lasttransaktion fragt f�r ein zuf�llig gew�hltes
	 * Konto den Kontostand ab.
	 * 
	 * @throws SQLException Bei einem Zugriffsfehler auf die Datenbank.
	 */
	public void kontostandsTX() throws SQLException {
		int accountID = generateAccountID();
		
		// R�ckgabewert wird im Benchmark nicht verwendet
		@SuppressWarnings("unused")
		int balance = accountBalanceTX.getAccountBalance(accountID);
	}

	/**
	 * Diese Transaktion zahlt einen bestimmten Betrag auf ein
	 * zuf�llig gew�hltes Konto ein. Dabei werden ein Branch und
	 * ein Teller ebenfalls zuf�llig gew�hlt und aktualisert. 
	 * Am Ende wird ein Eintrag in der History-Tabelle angelegt.
	 * 
	 * @throws SQLException Bei einem Zugriffsfehler auf die Datenbank.
	 */
	public void einzahlungsTX() throws SQLException {
		int accountID = generateAccountID();
		int tellerID = generateTellerID();
		int branchID = generateBranchID();
		int delta = generateDelta();
		
		// R�ckgabewert wird im Benchmark nicht verwendet
		@SuppressWarnings("unused")
		int newBalance = depositMoneyTX.depositMoney(accountID, tellerID,
				branchID, delta);
	}
	
	/**
	 * Diese Transaktion z�hlt history-Eintr�ge mit einem
	 * bestimmten Einzahlungsbetrag.
	 * 
	 * @throws SQLException Bei einem Zugriffsfehler auf die Datenbank.
	 */
	public void analyseTX() throws SQLException {
		int delta = generateDelta();
		
		// R�ckgabewert wird im Benchmark nicht verwendet
		@SuppressWarnings("unused")
		int count = countDeltaTX.countDeltaEntries(delta);
	}
	
	/**
	 * L�dt die konkreten Implementierungen der Lasttransaktionen auf Basis
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
	 * L�dt eine konkrete Implementierung einer Lasttransaktion und
	 * initialisiert diese.
	 * 
	 * @param paramName Klassenname der konkreten Implementierung.
	 * @return Lasttransaktion.
	 * @throws Exception Falls die Klasse nicht geladen werden konnte.
	 */
	private Transaction loadTransaction(String paramName) throws Exception {
		// Klasse �ber Reflection laden
		String className = config.getUser(paramName);
		Class<?> class_ = Class.forName(className);
		
		// Objekt der Klasse instantieren und initialisieren
		Transaction transaction = (Transaction)class_.newInstance();
		transaction.initialize(connection);
		
		return transaction;
	}
	
	/**
	 * Generiert eine zuf�llige ID f�r Branches.
	 * 
	 * @return Zuf�llige ID.
	 */
	private int generateBranchID() {
		int branchCount = scaleN;
		return random.nextInt(branchCount) + 1;
	}
	
	/**
	 * Generiert eine zuf�llige ID f�r Tellers.
	 * 
	 * @return Zuf�llige ID.
	 */
	private int generateTellerID() {
		int tellerCount = scaleN * FixedData.RATIO_TELLERS_TO_BRANCHES;
		return random.nextInt(tellerCount) + 1;
	}
	
	/**
	 * Generiert eine zuf�llige ID f�r Accounts.
	 * 
	 * @return Zuf�llige ID.
	 */
	private int generateAccountID() {
		int accountCount = scaleN * FixedData.RATIO_ACCOUNTS_TO_BRANCHES;
		return random.nextInt(accountCount) + 1;
	}
	
	/**
	 * Generiert einen zuf�lligen Einzahlungsbetrag.
	 * 
	 * @return Zuf�lliger Einzahlungsbetrag.
	 */
	private int generateDelta() {
		final int MAX_DELTA = 10000;
		return random.nextInt(MAX_DELTA) + 1;
	}
}
