package dbi.benchmark.insert;

/**
 * Diese Klasse enth�lt Konstanten, die f�r die Bef�llung der Datenbank
 * ben�tigt werden.
 *
 */
public final class FixedData {
	public static final int RATIO_ACCOUNTS_TO_BRANCHES = 100000;
	
	public static final int RATIO_TELLERS_TO_BRANCHES = 10;
	
	public static final String BRANCH_NAME = "Branch_ABCDEFGHIJKLM";
	
	public static final String BRANCH_ADDRESS = 
			"Westf�lische Hochschule, Stan. Bocholt, " +
			"M�nsterstra�e 265, 46397 Bocholt";
	
	public static final int BRANCH_BALANCE = 0;
	
	public static final String ACCOUNT_NAME = "Account_ABCDEFGHIJKL";
	
	public static final String ACCOUNT_ADDRESS =
			"Westf�lische Hochschule, Neidenburger Stra�e 43, " +
			"45897 Gelsenkirchen";
	
	public static final int ACCOUNT_BALANCE = 0;
	
	public static final String TELLER_NAME = "TELLER_ABCDEFGHIJKL";
	
	public static final String TELLER_ADDRESS =
			"Westf�lische Hochschule, Neidenburger Stra�e 43, " +
			"45897 Gelsenkirchen";
	
	public static final int TELLER_BALANCE = 0;
	
	static {
		// Damit die Benchmarkergebnisse vergleichbar sind, werden alle
		// Textdaten immer mit maximaler L�nge angelegt. Die L�ngen der
		// Stringkonstanten werden hier �berpr�ft, um Fehler zu vermeiden.
		assert (BRANCH_NAME.length() == 20);
		assert (BRANCH_ADDRESS.length() == 72);
		assert (ACCOUNT_NAME.length() == 20);
		assert (ACCOUNT_ADDRESS.length() == 68);
		assert (TELLER_NAME.length() == 20);
		assert (TELLER_ADDRESS.length() == 68);
	}
}
