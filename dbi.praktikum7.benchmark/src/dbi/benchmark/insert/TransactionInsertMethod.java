package dbi.benchmark.insert;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * Diese Klasse implementiert das Decorator-Pattern. Dabei wird eine bestehende
 * InsertMethod um eine umfassende Transaktion erweitert. Die Transaktion wird
 * gestartet, bevor die eigentlichen Daten eingef�gt werden. Nach erfolgreichem
 * Einf�gen wird die Transaktion comittet. Im Fehlerfall wird ein Rollback
 * ausgef�hrt.
 * 
 */
public class TransactionInsertMethod implements InsertMethod {
	private InsertMethod decoratedMethod;
	
	/**
	 * Erweitert eine InsertMethod um eine umfassende Transaktion.
	 * 
	 * @param method Methode, die erweitert werden soll.
	 */
	public TransactionInsertMethod(InsertMethod method) {
		this.decoratedMethod = method;
	}

	/**
	 * Vor dem Ausf�hren der Basis-Methode werden die Transaktion gestartet.
	 * Nach der Ausf�hrung wird entweder ein Commit oder Rollback abgesetzt
	 * (Erfolgreiche Ausf�hrung => Commit, Fehler => Rollback).
	 */
	@Override
	public void doInsert(int scaleN, Connection connection) throws SQLException {
		connection.setAutoCommit(false);
		try {
			decoratedMethod.doInsert(scaleN, connection);
			connection.commit();
		}
		catch (SQLException ex) {
			connection.rollback();
			throw ex;
		}
	}

}
