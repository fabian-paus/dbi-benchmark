package de.whs.dbi.loaddriver;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.sql.SQLException;
import java.util.concurrent.Callable;

import de.whs.dbi.benchmark.BenchmarkIF.STAGE;
import de.whs.dbi.util.Result;

/**
 * LoadDriverIF beschreibt die Schnittstelle eines Load Drivers.
 */
public interface LoadDriverIF extends Remote, Callable<Result>
{
	/**
	 * De-Registriert den LoadDriver.
	 * 
	 * @throws RemoteException
	 * @throws SQLException
	 */
	public void close() throws RemoteException, SQLException;

	/**
	 * Gibt die aktuelle Phase zurück.
	 * 
	 * @return Phase
	 * @throws RemoteException
	 */
	public STAGE getStage() throws RemoteException;

	/**
	 * Legt die aktuelle Phase fest.
	 * 
	 * @param stage Phase
	 * @throws RemoteException
	 */
	public void setStage(STAGE stage) throws RemoteException;
	
	// TODO Methode, um Zwischenresultat abzufragen=???
}
