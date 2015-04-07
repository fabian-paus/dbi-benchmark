package dbi.benchmark.copy;

import dbi.benchmark.insert.FixedData;

/**
 * Diese Klasse generiert Datenzeilen f�r die 'branches' Tabelle.
 */
public class BranchLineGenerator implements CopyLineGenerator {
	private int branchID;
	private int branchCount;
	
	/**
	 * Erstellt einen Zeilengenerator f�r die 'branches' Tabelle.
	 * 
	 * @param scaleN Skalierungsfaktor n.
	 */
	public BranchLineGenerator(int scaleN) {
		branchID = 0;
		branchCount = scaleN;
	}

	/**
	 * Das Ende ist erreicht, falls die Anzahl an einzuf�gender Branches
	 * erreicht ist.
	 */
	@Override
	public boolean isEndOfLines() {
		return branchID >= branchCount;
	}

	/**
	 * Die n�chste Datenzeile setzt sich aus der laufenden Account-ID, den
	 * festen Daten und einer generierten Branch-ID zusammen.
	 * 
	 * Aufbau:
	 * branchid [TAB] branchname [TAB] balance [TAB] address [NEWL]
	 */
	@Override
	public String getNextLine() {
		++branchID;
		return branchID + "\t" + 
			   FixedData.BRANCH_NAME + "\t"	+
			   FixedData.BRANCH_BALANCE + "\t" + 
			   FixedData.BRANCH_ADDRESS + "\n";
	}
}
