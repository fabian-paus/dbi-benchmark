package de.whs.dbi.loaddriver;

import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import de.whs.dbi.util.Configuration;

/**
 * Die abstrakte Klasse Database kapselt den Datenbankzugriff zu unterschiedlichen
 * DBMSs und bietet dazu eine einheitliche Schnittstelle an.
 */
public abstract class Database
{
	/**
	 * Datenbankverbindung
	 */
	protected Connection connection;
	
	/**
	 * Konfiguration
	 */
	protected Configuration config;
	
	/**
	 * Der Konstruktor initialisiert die Datenbankverbindung.
	 * 
	 * @param config Konfiguration
	 * @throws SQLException 
	 */
	public Database(Configuration config) throws SQLException
	{
		this.config = config;
		openConnection();
		init();
	}

	/**
	 * Stellt die Datenbankverbindung her und deaktiviert AutoCommit.
	 * 
	 * @throws SQLException
	 */
	protected void openConnection() throws SQLException
	{
		connection = DriverManager.getConnection(config.getDatabaseJDBC());
	}
	
	/**
	 * Schließt die Datenbankverbindung.
	 * 
	 * @throws SQLException
	 */
	public void closeConnection() throws SQLException
	{
		connection.close();
	}
	
	/**
	 * Zusätzliche Möglichkeit zur Initialisierung nach dem Verbindungsaufbau.
	 * 
	 * @throws SQLException 
	 */
	public void init() throws SQLException
	{
		connection.setAutoCommit(false);
	}

	/**
	 * Beginnt eine Transaktion.
	 * 
	 * @throws SQLException
	 */
	public void beginTransaction() throws SQLException
	{

	}

	/**
	 * Beendet eine Transaktion.
	 * 
	 * @throws SQLException
	 */
	public void commitTransaction() throws SQLException
	{
		connection.commit();
	}

	/**
	 * Setzt eine Transaktion zurück.
	 * 
	 * @throws SQLException
	 */
	public void rollbackTransaction() throws SQLException
	{
		connection.rollback();
	}

	/**
	 * Erstellt ein Transaktionsobjekt zu einem vorgegebenen Methodennamen aus
	 * dem Lastprofil.
	 * 
	 * @param transactionName vorgegebener Methodenname
	 * @return Transaktion
	 * @throws NoSuchMethodException
	 * @throws SecurityException
	 */
	public final Transaction createTransaction(String transactionName) throws NoSuchMethodException, SecurityException
	{
		Method method = getClass().getMethod(transactionName);
		return new Transaction(this, method, config.getBenchmarkTime());
	}
	
}
