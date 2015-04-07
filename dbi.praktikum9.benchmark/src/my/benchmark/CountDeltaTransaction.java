package my.benchmark;

import java.sql.SQLException;

/**
 * Diese Lasttransaktion f�hrt eine Auswertung �ber die History.
 *
 */
public interface CountDeltaTransaction  extends Transaction {
	/**
	 * Z�hlt alle History-Eintr�ge mit einem bestimmten Einzahlungsbetrag.
	 * 
	 * @param delta Einzahlungsbetrag.
	 * @return Anzahl der History-Eintr�ge.
	 * @throws SQLException Bei einem Zugriffsfehler auf die Datenbank.
	 */
	int countDeltaEntries(int delta) throws SQLException;
}
