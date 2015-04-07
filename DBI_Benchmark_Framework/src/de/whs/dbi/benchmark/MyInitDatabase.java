package de.whs.dbi.benchmark;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import de.whs.dbi.util.Configuration;

/**
 * MyInitDatabase ist eine weiter auszuprogrammierende Klasse zur Initialisierung der Datenbank.
 */
public class MyInitDatabase 
{
	/**
	 * Datenbankverbindung
	 */
	protected static Connection connection;
	
	/**
	 * Konfiguration
	 */
	protected static Configuration config;
	
	/**
	 * Stellt eine Datenbankverbindung her und deaktiviert AutoCommit.
	 * Die ben�tigte JDBC-URL wird aus der Konfiguration gelesen.
	 * 
	 * @throws SQLException
	 */
	protected static void openConnection() throws SQLException 
	{
		connection = DriverManager.getConnection(config.getDatabaseJDBC());
		connection.setAutoCommit(false);
	}
	
	/**
	 * Schlie�t die Datenbankverbindung.
	 * 
	 * @throws SQLException
	 */
	protected static void closeConnection() throws SQLException
	{
		connection.close();
	}
	
	/**
	 * L�scht, falls vorhanden, die bisherigen Tabellen.
	 * 
	 * @throws SQLException
	 */
	protected static void dropTables() throws SQLException
	{
		
	}
	
	/**
	 * Erstellt die Tabellen.
	 * 
	 * @throws SQLException
	 */
	protected static void createTables() throws SQLException 
	{
		
	}

	/**
	 * F�hrt optimierende Konfigurationseinstellungen im DBMS
	 * und an der Datenbank durch.
	 * 
	 * @throws SQLException
	 */
	protected static void tuneDatabase() throws SQLException 
	{
		
	}

	/**
	 * Legt die neuen Datens�tze an.
	 * 
	 * @throws SQLException
	 */
	protected static void insertRows() throws SQLException 
	{
		
	}

	/**
	 * Hauptprogramm zum Starten der Initialisierung der Datenbank.
	 * 
	 * @param args
	 */
	public static void main(String[] args)
	{	
		try
		{
			System.out.println("Initialisierung der Datenbank:\n");

			// L�dt die Konfiguration
			config = new Configuration();
			config.loadBenchmarkConfiguration();
			
			openConnection();
			System.out.println("L�schen vorhandener Relationen ...");
			dropTables();
			System.out.println("Anlegen des Datenbankschemas ...");
			createTables();
			System.out.println("Durchf�hrung von Tuning-Einstellungen ...");
			tuneDatabase();
			System.out.println("Einf�gen der Tupel ...");
			
			// Merken des Start-Zeitpunktes der Initialisierung
			long duration = System.currentTimeMillis();
			
			insertRows();
			
			// Berechnung der Zeitdauer der Initialisierung
			duration = System.currentTimeMillis()-duration;
			
			closeConnection();

			System.out.println("Einf�gedauer: " + duration/1000 + " Sekunden\n");
		} catch (Exception e)
		{
			e.printStackTrace();
		}
	}
}
