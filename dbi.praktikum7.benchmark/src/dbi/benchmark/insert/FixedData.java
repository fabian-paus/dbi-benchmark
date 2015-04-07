package dbi.benchmark.insert;

/**
 * Diese Klasse enthält Konstanten, die für die Befüllung der Datenbank
 * benötigt werden.
 *
 */
public final class FixedData {
	public static final int RATIO_ACCOUNTS_TO_BRANCHES = 100000;
	
	public static final int RATIO_TELLERS_TO_BRANCHES = 10;
	
	public static final String BRANCH_NAME = "Branch_ABCDEFGHIJKLM";
	
	public static final String BRANCH_ADDRESS = 
			"Westfälische Hochschule, Stan. Bocholt, " +
			"Münsterstraße 265, 46397 Bocholt";
	
	public static final int BRANCH_BALANCE = 0;
	
	public static final String ACCOUNT_NAME = "Account_ABCDEFGHIJKL";
	
	public static final String ACCOUNT_ADDRESS =
			"Westfälische Hochschule, Neidenburger Straße 43, " +
			"45897 Gelsenkirchen";
	
	public static final int ACCOUNT_BALANCE = 0;
	
	public static final String TELLER_NAME = "TELLER_ABCDEFGHIJKL";
	
	public static final String TELLER_ADDRESS =
			"Westfälische Hochschule, Neidenburger Straße 43, " +
			"45897 Gelsenkirchen";
	
	public static final int TELLER_BALANCE = 0;
	
	static {
		// Damit die Benchmarkergebnisse vergleichbar sind, werden alle
		// Textdaten immer mit maximaler Länge angelegt. Die Längen der
		// Stringkonstanten werden hier überprüft, um Fehler zu vermeiden.
		assert (BRANCH_NAME.length() == 20);
		assert (BRANCH_ADDRESS.length() == 72);
		assert (ACCOUNT_NAME.length() == 20);
		assert (ACCOUNT_ADDRESS.length() == 68);
		assert (TELLER_NAME.length() == 20);
		assert (TELLER_ADDRESS.length() == 68);
	}
}
