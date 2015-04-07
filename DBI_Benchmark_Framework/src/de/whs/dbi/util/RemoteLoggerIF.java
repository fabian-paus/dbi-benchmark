package de.whs.dbi.util;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.logging.LogRecord;

/**
 * RemoteLoggerIF bescheibt die Schnittstelle eines Remote Loggers.
 */
public interface RemoteLoggerIF extends Remote
{
	/**
	 * Gibt ein LogRecord an den Logger weiter.
	 * 
	 * @param record LogRecord
	 * @throws RemoteException
	 */
	public void logMessage(LogRecord record) throws RemoteException;
}
