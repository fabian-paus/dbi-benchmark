package de.whs.dbi.util;

import java.io.IOException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

/**
 * Ein Remote Logger empfängt und verarbeitet Protokollmeldungen über RMI.
 */
public class RemoteLogger extends UnicastRemoteObject implements RemoteLoggerIF
{
	
	private static final long serialVersionUID = 887245597519002664L;

	protected Logger logger;
	protected FileHandler handler;

	/**
	 * Der Konstruktor initialisiert den Remote-Logger.
	 * 
	 * @param level Log-Level
	 * @throws SecurityException
	 * @throws IOException
	 */
	public RemoteLogger(Level level) throws SecurityException, IOException
	{
		logger = Logger.getLogger("de.whs.dbi");
		handler = new FileHandler("logs/benchmark_" + new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date()) + ".log");
		logger.addHandler(handler);
		logger.setLevel(level);
	}

	@Override
	public synchronized void logMessage(LogRecord record) throws RemoteException
	{
		logger.log(record);
	}

}
