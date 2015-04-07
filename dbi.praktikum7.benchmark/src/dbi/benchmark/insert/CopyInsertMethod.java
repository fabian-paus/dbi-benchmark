package dbi.benchmark.insert;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import org.postgresql.copy.CopyManager;
import org.postgresql.core.BaseConnection;

import dbi.benchmark.copy.AccountsLineGenerator;
import dbi.benchmark.copy.BranchLineGenerator;
import dbi.benchmark.copy.CopyInputStream;
import dbi.benchmark.copy.TellersLineGenerator;

/**
 * Diese Klasse implementiert das Einfügen von Daten über den COPY Befehl.
 * Dieser Befehl ist PostgreSQL spezifisch und dient zum schnellen Einfügen von
 * sehr großen Datenmengen.
 *
 */
public class CopyInsertMethod implements InsertMethod {
	
	private final String COPY_BRANCHES = 
			"COPY branches(branchid, branchname, balance, address) " +
			"FROM STDIN";
	private final String COPY_ACCOUNTS =
			"COPY accounts(accid, name, balance, branchid, address)" +
			"FROM STDIN";
	private final String COPY_TELLERS =
			"COPY tellers(tellerid, tellername, balance, branchid, address)" +
			"FROM STDIN";

	/**
	 * Fügt Daten über den COPY Befehl in die Datenbank ein.
	 */
	@Override
	public void doInsert(int scaleN, Connection connection) throws SQLException {
		// Die CopyManager-Klasse ist PostgreSQL spezifisch. Dieser Methode
		// kann also nicht für andere DBMS eingesetzt werden.
		CopyManager copyManager = new CopyManager((BaseConnection) connection);

		CopyInputStream source = new CopyInputStream();
		try {
			source.setGenerator(new BranchLineGenerator(scaleN));
			copyManager.copyIn(COPY_BRANCHES, source);

			source.setGenerator(new AccountsLineGenerator(scaleN));
			copyManager.copyIn(COPY_ACCOUNTS, source);

			source.setGenerator(new TellersLineGenerator(scaleN));
			copyManager.copyIn(COPY_TELLERS, source);
		} catch (IOException ex) {
			// Diese Ausnahme wird in dem selbst implementierten InputStream
			// geworfen, falls die Daten nicht nach UTF-8 kodiert werden
			// konnten. In dem Fall kann nicht sinnvoll gehandelt werden.
			// Das Program wird mit einer Fehlermeldung beendet.
			throw new Error("No UTF-8 Encoding found", ex);
		}
	}

}
