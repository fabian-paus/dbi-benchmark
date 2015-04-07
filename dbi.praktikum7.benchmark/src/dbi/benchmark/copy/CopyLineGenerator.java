package dbi.benchmark.copy;

/**
 * Diese Schnittstelle definiert eine Quelle von Datenzeilen, die für
 * den COPY Befehl verwendet werden können.
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
	 * Überprüft, ob das Ende aller Zeilen erreicht wurde.
	 * 
	 * @return true, wenn keine Zeilen mehr generiert werden können, 
	 *         andernfalls false.
	 */
	boolean isEndOfLines();
	
	/**
	 * Liest die nächste Datenzeile aus.
	 * 
	 * @return Nächste Datenzeile.
	 */
	String getNextLine();
}
