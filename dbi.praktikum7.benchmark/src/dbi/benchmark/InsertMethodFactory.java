package dbi.benchmark;

import dbi.benchmark.insert.*;

/**
 * Diese Klasse ist eine Factory, die für eine gegebene ID eine InsertMethod
 * erzeugt. Die ID kann z.B. aus einer Konfigurationsdatei gelesen werden.
 * Die Methode, mit der die Datenbank gefüllt werden soll, kann so zur Laufzeit
 * bestimmt werden.
 * 
 */
public final class InsertMethodFactory {
	/**
	 * Erstellt eine InsertMethod für die gegebene ID.
	 * 
	 * Mögliche IDs:
	 * - 1: Einfache INSERT Befehle über einzelne Statements abgesetzt
	 * - 2: Wie 1, nur dass die INSERT Befehle mit einer Transaktion 
	 *      zusammengefasst werden
	 * - 3: Einfache INSERT Befehle über PreparedStatements abgesetzt
	 * - 4: Wie 3, nur dass die INSERT Befehler mit einer Transaktion
	 *      zusammengefasst werden
	 * - 5: Bulk-Insert über den PostgreSQL spezifischen COPY Befehl
	 * - 6: Wie 5, nur dass vor den COPY Befehlen die Foreign-Key-Constaints
	 *      entfernt werden und nach Einfügen der Daten wieder hinzugefügt
	 *      werden
	 * 
	 * Nicht genannte IDs sind ungültig. Wird eine ungültige ID angegeben,
	 * dann wird eine Exception geworfen.
	 * 
	 * @param id ID der InsertMethod.
	 * @return Neue Instanz einer InsertMethod oder null bei ungültiger ID.
	 * @throws Exception Bei einer ungültigen ID.
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
