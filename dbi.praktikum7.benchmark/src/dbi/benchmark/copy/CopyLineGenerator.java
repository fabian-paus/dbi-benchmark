package dbi.benchmark.copy;

/**
 * Diese Schnittstelle definiert eine Quelle von Datenzeilen, die f�r
 * den COPY Befehl verwendet werden k�nnen.
 * 
 * Eine Datenzeile hat dabei folgenden Aufbau:
 * 
 * column1 [TAB] column2 [TAB] column3 [TAB] column4 [NEWL]
 * 
 * Dabei werden die einzelnen Spaltendaten durch ein Tabulatorzeichen
 * getrennt. Eine Zeile wird mit einem Zeilenumbruch beendet.
 *
 */
public interface CopyLineGenerator {
	/**
	 * �berpr�ft, ob das Ende aller Zeilen erreicht wurde.
	 * 
	 * @return true, wenn keine Zeilen mehr generiert werden k�nnen, 
	 *         andernfalls false.
	 */
	boolean isEndOfLines();
	
	/**
	 * Liest die n�chste Datenzeile aus.
	 * 
	 * @return N�chste Datenzeile.
	 */
	String getNextLine();
}
