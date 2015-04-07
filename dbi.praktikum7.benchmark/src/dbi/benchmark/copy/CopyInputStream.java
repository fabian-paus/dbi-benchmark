package dbi.benchmark.copy;

import java.io.IOException;
import java.io.InputStream;

/**
 * Diese Klasse ist eine eigene Implementierung von java.io.InputStream.
 * Sie wird dazu verwendet die Datenzeilen für einen COPY Befehl on-the-fly
 * und in-memory zu erzeugen. Anstatt die Daten erst zu generieren, dann in
 * eine Datei zu schreiben und anschließend an den Datenbankserver zu senden,
 * können die Daten im Hauptspeicher erzeugt werden und direkt (bzw. in
 * gewissen Blöcken) an den Datenbankserver gesendet werden.
 * 
 * Die Implementierung verwendet das Strategy-Pattern um die Generierung der
 * Datenzeilen dynamsich zu halten. Die gesendeten Datenzeilen werden über
 * eine konkrete Implementierung der Schnittstelle CopyLineGenerator bestimmt.
 * Auf diese Weise kann diese Klasse zum Senden von Datenzeilen beliebigen
 * Typs verwendet werden.
 *
 */
public class CopyInputStream extends InputStream {
	protected byte[] buffer;
	private int position;
	private CopyLineGenerator generator;
	
	/**
	 * Setzt den verwendeten Zeilengenerator.
	 * Das neu Setzen des Generators setzt auch den Stream zurück, so dass 
	 * dieser wieder zum Lesen neuer Daten verwendet werden kann.
	 * 
	 * @param generator Zeilengenerator.
	 * @throws IOException Bei Kodierungsfehlern.
	 */
	public void setGenerator(CopyLineGenerator generator) throws IOException {
		this.generator = generator;
		
		// Hier den Buffer inital füllen, damit der Stream nicht beim ersten
		// Leseversuch als leer erkannt wird.
		nextBuffer();
	}

	/**
	 * Liest ein einzelnes Byte. Verwendet read(byte[] b, int off, int len).
	 */
	@Override
	public int read() throws IOException {
		byte[] b = new byte[1];
		int result = read(b, 0, 1);
		if (result == -1)
			return -1;
		return b[0];
	}
	
	/**
	 * Liest ein Byte-Array. Verwendet read(byte[] b, int off, int len).
	 */
	@Override
	public int read(byte[] b) throws IOException {
		return read(b, 0, b.length);
	}

	/**
	 * Diese Stream-Implementierung erzeugt die Daten genau dann, wenn diese
	 * benötigt werden. D.h. Es wird zunächst der aktuelle Bufferinhalt
	 * verwendet. Ist dieser erschöpft, wird über den CopyLineGenerator eine
	 * neue Datenzeile erzeugt, die in den Buffer geladen wird. Das geschieht
	 * solange, bis keine Zeilen mehr verfügbar sind.
	 */
	@Override
	public int read(byte[] b, int off, int len) throws IOException {
		// Wenn es keine Bytes mehr zu schreiben gibt, dann wird mit -1
		// angezeigt, dass der Stream keine Daten mehr liefert
		if (isEndOfInput())
			return -1;
		
		int bytesRead = 0;
		
		// Solange noch nicht alle Bytes aus dem aktuellen Puffer geschrieben
		// wurden und noch weitere Daten generiert werden können, werden
		// weitere Daten geschrieben
		while (len != 0 && !isEndOfInput()) {
			// Bytes aus dem Puffer in das Zielarray kopieren, dabei können
			// maximal die verbleibenden Bytes im Puffer geschrieben werden
			int copyCount = Math.min(buffer.length - position, len);
			System.arraycopy(buffer, position, b, off, copyCount);
			position += copyCount;
			
			// Wenn der aktuelle Puffer vollständig rausgeschrieben wurde,
			// muss ein neuer generiert werden
			if (!hasRemainingBuffer())
				nextBuffer();
			
			// Es wurde eine bestimmte Anzahl von Bytes geschrieben
			// Die Schreibposition und die noch zu schreibende Menge
			// müssen also angepasst werden
			bytesRead += copyCount;
			off += copyCount;
			len -= copyCount;
		} 
		
		return bytesRead;
	}
	
	/**
	 * Überprüft, ob das Stream-Ende erreicht wurde.
	 * 
	 * @return true, bei Stream-Ende, ansonsten false.
	 */
	private boolean isEndOfInput() {
		return !hasRemainingBuffer() && generator.isEndOfLines();
	}
	
	/**
	 * Überprüft, ob der aktuelle Buffer noch Bytes enthält.
	 * 
	 * @return false, wenn der Buffer leer ist, ansonsten true.
	 */
	private boolean hasRemainingBuffer() {
		return position < buffer.length;
	}
	
	/**
	 * Überprüft, ob ein nächste Buffer vom Generator erzeugt werden kann.
	 * 
	 * @return true, wenn es noch Buffer gibt, ansonsten false.
	 */
	private boolean hasNextBuffer() {
		return !generator.isEndOfLines();
	}
	
	/**
	 * Verwendet den CopyLineGenerator um den nächsten Buffer zu erzeugen.
	 * 
	 * @throws IOException Bei einem Kodierungsfehler.
	 */
	private void nextBuffer() throws IOException {
		if (!hasNextBuffer())
			return;
		
		String line = generator.getNextLine();
		buffer = line.getBytes("UTF-8");
		position = 0;	
	}
}
