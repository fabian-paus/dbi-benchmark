package my.benchmark;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * Allgemeine Schnittstelle f�r eine Lasttransaktion.
 * 
 */
public interface Transaction {
	/**
	 * Initialisiert die Lasttransaktion.
	 * 
	 * @param connection Datenbankverbindung.
	 * @throws SQLException Bei einem Zugriffsfehler auf die Datenbank.
	 */
	void initialize(Connection connection) throws SQLException;
	
	/**
	 * Gibt die verwendeten Ressourcen der Lasttransaktion frei.
	 * 
	 * @throws SQLException Bei einem Zugriffsfehler auf die Datenbank.
	 */
	void close() throws SQLException;
}
