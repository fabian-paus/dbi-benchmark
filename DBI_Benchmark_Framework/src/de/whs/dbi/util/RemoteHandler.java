package de.whs.dbi.util;

import java.rmi.RemoteException;
import java.util.logging.ErrorManager;
import java.util.logging.Handler;
import java.util.logging.LogRecord;

/**
 * Ein Remote Handler ist ein Log Handler, der alle Logmeldungen an den
 * Remote Logger weiterleitet.
 */
public class RemoteHandler extends Handler
{
	/**
	 * Remote-Logger
	 */
	protected RemoteLoggerIF logger;

	/**
	 * Der Konstruktor initialisiert den Remote-Handler.
	 * 
	 * @param logger Remote-Logger
	 */
	public RemoteHandler(RemoteLoggerIF logger)
	{
		this.logger = logger;
	}

	@Override
	public void close() throws SecurityException
	{

	}

	@Override
	public void flush()
	{

	}

	@Override
	public void publish(LogRecord arg0)
	{
		try
		{
			logger.logMessage(arg0);
		} catch (RemoteException e)
		{
			reportError(null, e, ErrorManager.WRITE_FAILURE);
		}
	}

}
