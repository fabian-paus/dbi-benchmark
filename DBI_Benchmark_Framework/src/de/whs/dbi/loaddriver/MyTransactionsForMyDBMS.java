package de.whs.dbi.loaddriver;

import java.sql.SQLException;

import de.whs.dbi.util.Configuration;

/**
 * MyTransactionsForMyDBMS ist eine weiter auszuprogrammierende Unterklasse von
 * Database. Diese Klasse enthält die Benchmark-spezifischen Lasttransaktionen
 * sowie die DBMS-spezifischen Anpassungen bei überschriebenen Methoden aus
 * Database.
 */
public class MyTransactionsForMyDBMS extends Database
{
	/**
	 * Der Konstruktor initialisiert die Datenbankverbindung.
	 * 
	 * @param config Konfiguration
	 * @throws SQLException
	 */
	public MyTransactionsForMyDBMS(Configuration config) throws SQLException
	{
		super(config);
	}

	/**
	 * Eine Transaktion, die in der Konfiguration als Beispiel definiert ist.
	 */
	public void exampleTransaction()
	{

	}

}
