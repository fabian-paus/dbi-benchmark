package dbi.benchmark.copy;

import java.util.Random;
import dbi.benchmark.insert.FixedData;

/**
 * Diese Klasse generiert Datenzeilen für die 'tellers' Tabelle.
 */
public class TellersLineGenerator implements CopyLineGenerator {
	private int tellerID;
	private int branchCount;
	private int tellerCount;
	private Random random;
	
	/**
	 * Erstellt einen Zeilengenerator für die 'tellers' Tabelle.
	 * 
	 * @param scaleN Skalierungsfaktor n.
	 */
	public TellersLineGenerator(int scaleN) {
		tellerID = 0;
		branchCount = scaleN;
		tellerCount = branchCount * FixedData.RATIO_TELLERS_TO_BRANCHES;
		random = new Random();
	}

	/**
	 * Das Ende ist erreicht, falls die Anzahl an einzufügender Tellers
	 * erreicht ist.
	 */
	@Override
	public boolean isEndOfLines() {
		return tellerID >= tellerCount;
	}

	/**
	 * Die nächste Datenzeile setzt sich aus der laufenden Account-ID, den
	 * festen Daten und einer generierten Branch-ID zusammen.
	 * 
	 * Aufbau: 
	 * tellerid [TAB] tellername [TAB] balance [TAB] branchid [TAB]
	 * address [NEWL]
	 */
	@Override
	public String getNextLine() {
		++tellerID;
		return tellerID + "\t" + 
			   FixedData.TELLER_NAME + "\t"	+
			   FixedData.TELLER_BALANCE + "\t" + 
			   generateBranchID() + "\t" +
			   FixedData.TELLER_ADDRESS + "\n";
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
