package dbi.benchmark.copy;

import java.util.Random;
import dbi.benchmark.insert.FixedData;

/**
 * Diese Klasse generiert Datenzeilen für die 'accounts' Tabelle.
 */
public class AccountsLineGenerator implements CopyLineGenerator {
	private int accountID;
	private int branchCount;
	private int accountCount;
	private Random random;
	
	/**
	 * Erstellt einen Zeilengenerator für die 'accounts' Tabelle.
	 * 
	 * @param scaleN Skalierungsfaktor n.
	 */
	public AccountsLineGenerator(int scaleN) {
		accountID = 0;
		branchCount = scaleN;
		accountCount = branchCount * FixedData.RATIO_ACCOUNTS_TO_BRANCHES;
		random = new Random();
	}

	/**
	 * Das Ende ist erreicht, falls die Anzahl an einzufügender Accounts
	 * erreicht ist.
	 */
	@Override
	public boolean isEndOfLines() {
		return accountID >= accountCount;
	}

	/**
	 * Die nächste Datenzeile setzt sich aus der laufenden Account-ID, den
	 * festen Daten und einer generierten Branch-ID zusammen.
	 * 
	 * Aufbau:
	 * accid [TAB] name [TAB] balance [TAB] branchid [TAB] address [NEWL]
	 */
	@Override
	public String getNextLine() {
		assert !isEndOfLines();

		++accountID;
		return accountID + "\t" + 
			   FixedData.ACCOUNT_NAME + "\t"	+
			   FixedData.ACCOUNT_BALANCE + "\t" + 
			   generateBranchID() + "\t" +
			   FixedData.ACCOUNT_ADDRESS + "\n";
	}
	
	/**
	 * Erzeugt eine zufällige Branch-ID.
	 * 
	 * @return Zufällige Branch-ID.
	 */
	private int generateBranchID() {
		return random.nextInt(branchCount) + 1;
	}
}
