package my.benchmark;

import java.sql.SQLException;

/**
 * Diese Lasttransaktion führt eine Kontostandsabfrage durch.
 *
 */
public interface AccountBalanceTransaction extends Transaction {
	/**
	 * Fragt den Kontostand in Accounts ab.
	 * 
	 * @param accountID Kontonummer.
	 * @return Kontostand.
	 * @throws SQLException Bei einem Zugriffsfehler auf die Datenbank.
	 */
	int getAccountBalance(int accountID) throws SQLException;
}
