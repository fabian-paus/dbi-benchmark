package my.benchmark;

import java.sql.SQLException;

/**
 * Diese Lasttransaktion führt einen Einzahlungsvorgang durch.
 * 
 */
public interface DepositMoneyTransaction extends Transaction {
	/**
	 * Die Transaktion führt folgende Einzelaktionen durch:
	 *  - Bilanzsumme in Branches aktualisieren
	 *  - Bilanzsumme in Tellers aktualisieren
	 *  - Kontostand in Accounts aktualisieren
	 *  - Einzahlung in History protokollieren
	 * 
	 * @param accountID Kontonummer.
	 * @param tellerID Geldautomatennummer.
	 * @param branchID Zweigstellennummer.
	 * @param delta Einzahlungsbetrag.
	 * @return Aktualsierter Kontostand.
	 * @throws SQLException Bei einem Zugriffsfehler auf die Datenbank.
	 */
	int depositMoney(int accountID, int tellerID, int branchID, int delta)
			throws SQLException;
}
