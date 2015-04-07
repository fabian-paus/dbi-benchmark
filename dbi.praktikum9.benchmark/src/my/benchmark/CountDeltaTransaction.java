package my.benchmark;

import java.sql.SQLException;

/**
 * Diese Lasttransaktion fährt eine Auswertung über die History.
 *
 */
public interface CountDeltaTransaction  extends Transaction {
	/**
	 * Zählt alle History-Einträge mit einem bestimmten Einzahlungsbetrag.
	 * 
	 * @param delta Einzahlungsbetrag.
	 * @return Anzahl der History-Einträge.
	 * @throws SQLException Bei einem Zugriffsfehler auf die Datenbank.
	 */
	int countDeltaEntries(int delta) throws SQLException;
}
