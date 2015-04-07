package dbi.benchmark;

import dbi.benchmark.insert.*;

/**
 * Diese Klasse ist eine Factory, die f�r eine gegebene ID eine InsertMethod
 * erzeugt. Die ID kann z.B. aus einer Konfigurationsdatei gelesen werden.
 * Die Methode, mit der die Datenbank gef�llt werden soll, kann so zur Laufzeit
 * bestimmt werden.
 * 
 */
public final class InsertMethodFactory {
	/**
	 * Erstellt eine InsertMethod f�r die gegebene ID.
	 * 
	 * M�gliche IDs:
	 * - 1: Einfache INSERT Befehle �ber einzelne Statements abgesetzt
	 * - 2: Wie 1, nur dass die INSERT Befehle mit einer Transaktion 
	 *      zusammengefasst werden
	 * - 3: Einfache INSERT Befehle �ber PreparedStatements abgesetzt
	 * - 4: Wie 3, nur dass die INSERT Befehler mit einer Transaktion
	 *      zusammengefasst werden
	 * - 5: Bulk-Insert �ber den PostgreSQL spezifischen COPY Befehl
	 * - 6: Wie 5, nur dass vor den COPY Befehlen die Foreign-Key-Constaints
	 *      entfernt werden und nach Einf�gen der Daten wieder hinzugef�gt
	 *      werden
	 * 
	 * Nicht genannte IDs sind ung�ltig. Wird eine ung�ltige ID angegeben,
	 * dann wird eine Exception geworfen.
	 * 
	 * @param id ID der InsertMethod.
	 * @return Neue Instanz einer InsertMethod oder null bei ung�ltiger ID.
	 * @throws Exception Bei einer ung�ltigen ID.
	 */
	public static InsertMethod create(int id) {
		InsertMethod simpleInsert = new SimpleInsertMethod();
		InsertMethod preparedInsert = new PreparedInsertMethod();
		InsertMethod copyInsert = new CopyInsertMethod();
		
		switch (id) {
		case 1: return simpleInsert;
		case 2: return new TransactionInsertMethod(simpleInsert);
		
		case 3: return preparedInsert;
		case 4: return new TransactionInsertMethod(preparedInsert);
		
		case 5: return copyInsert;
		case 6: return new ConstraintStripMethod(copyInsert);
		
		default: throw new RuntimeException("Unknown method ID: " + id);
		}
	}
}
