package de.whs.dbi.benchmark;

import java.rmi.AlreadyBoundException;
import java.rmi.Remote;
import java.rmi.RemoteException;

import de.whs.dbi.loaddriver.LoadDriverIF;
import de.whs.dbi.util.Configuration;
import de.whs.dbi.util.RemoteLoggerIF;

/**
 * BenchmarkIF beschreibt die Schnittstelle eines Benchmark Controllers.
 */
public interface BenchmarkIF extends Remote
{
	/**
	 * Aufzählungstyp für die möglichen Phasen des Benchmarks
	 */
	public static enum STAGE
	{
		INIT, READY, WARMUP, BENCHMARK, COOLDOWN, FINISHED
	}

	/**
	 * Registriert einen LoadDriver im Benchmark und gibt eine eindeutige ID für den LoadDriver zurück.
	 * 
	 * @param loadDriver zu registrierendes LoadDriver-Objekt
	 * @return ID des LoadDrivers
	 * @throws RemoteException
	 * @throws AlreadyBoundException
	 */
	public int registerLoadDriver(LoadDriverIF loadDriver) throws RemoteException, AlreadyBoundException;

	/**
	 * Stellt die genutzte Konfiguration zur Verfügung.
	 * 
	 * @return Configuration
	 * @throws RemoteException
	 */
	public Configuration getConfiguration() throws RemoteException;

	/**
	 * Stellt den RemoteLogger zur Verfügung.
	 * 
	 * @return RemoteLoggerIF
	 * @throws RemoteException
	 */
	public RemoteLoggerIF getRemoteLogger() throws RemoteException;
}
