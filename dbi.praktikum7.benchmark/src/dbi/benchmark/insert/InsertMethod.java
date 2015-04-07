package dbi.benchmark.insert;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * Eine Implementierung der InsertMethod Schnittstelle füllt in seiner
 * doInsert Methode die Datenbank mit der geforderten Datenmenge.
 *
 */
public interface InsertMethod {
	/**
	 * Füllt die Datenbank mit der geforderten Datenmenge.
	 * 
	 * @param scaleN Skalierungsfaktor n.
	 * @param connection Datenbankverbindung.
	 * @throws SQLException Bei Zugriffsfehler auf die Datenbank.
	 */
	public void doInsert(int scaleN, Connection connection) throws SQLException;
}
