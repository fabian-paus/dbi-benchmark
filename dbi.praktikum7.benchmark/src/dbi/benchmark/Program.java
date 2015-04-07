package dbi.benchmark;

/**
 * Diese Klasse beinhaltet das Hauptprogramm des Benchmarks.
 */
public class Program {

	/**
	 * Lädt die zentrale Konfiguration und führt einen Benchmark aus.
	 * Nach erfolgreicher Ausführung des Benchmarks wird die benötigte
	 * Zeit ausgegeben.
	 * 
	 * @param args Kommandozeilenparameter werden nicht ausgewertet.
	 */
	public static void main(String[] args) {
		try {
			Benchmarker benchmarker = null;
			Configuration configuration = Configuration.load();
			try {
				benchmarker = new Benchmarker(configuration);
				benchmarker.execute();
				System.out.println("Benötigte Zeit: " 
						+ benchmarker.getElapsedTime() + "ms");
			}
			finally {
				if (benchmarker != null)
					benchmarker.close();
			}
		} catch (Exception e) {
			// An dieser Stelle werden alle Exceptions zentral abgefangen.
			// Es wird der Stack-Trace ausgegeben und das Programm wird
			// beendet. Auf eine genauere Fehlerbehandlung wird zugunsten
			// der Einfachheit verzichtet.
			e.printStackTrace();
		}
	}
}
