package de.whs.dbi.benchmark;

import java.rmi.AccessException;
import java.rmi.AlreadyBoundException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.logging.ConsoleHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;

import de.whs.dbi.loaddriver.LoadDriverIF;
import de.whs.dbi.util.Configuration;
import de.whs.dbi.util.RemoteLogger;
import de.whs.dbi.util.RemoteLoggerIF;
import de.whs.dbi.util.Result;

/**
 * Benchmark realisiert den Benchmark Controller.
 */
public class Benchmark extends UnicastRemoteObject implements BenchmarkIF
{
	private static final long serialVersionUID = 5637573890009643976L;

	protected Configuration config;
	protected ArrayList<LoadDriverIF> loaddrivers;
	protected Registry registry;
	protected RemoteLoggerIF remoteLogger;
	protected Logger log;

	/**
	 * Der Konstruktor initialisiert den Benchmark, sodass die LoadDriver gestartet werden können.
	 * 
	 * @throws Exception
	 */
	public Benchmark() throws Exception
	{
		config = new Configuration();
		config.loadRMIConfiguration();
		config.loadBenchmarkConfiguration();
		config.loadTransactionsConfiguration();

		remoteLogger = new RemoteLogger(config.getLogLevel());
		log = Logger.getLogger(Benchmark.class.getCanonicalName());
		for (Handler handler : Logger.getLogger("").getHandlers())
		{
			if (handler instanceof ConsoleHandler)
			{
				handler.setLevel(Level.WARNING);
			}
		}

		config.logBenchmarkConfiguration(log);
		config.logTransactionConfiguration(log);

		loaddrivers = new ArrayList<LoadDriverIF>();

		startRegistry();
	}

	/**
	 * Startet die RMI-Registry.
	 * 
	 * @throws RemoteException
	 * @throws AlreadyBoundException
	 */
	protected void startRegistry() throws RemoteException, AlreadyBoundException
	{
		registry = LocateRegistry.createRegistry(config.getRegistryPort());
		registry.bind(Configuration.BENCHMARK_NAME, this);
	}

	/**
	 * Stoppt die RMI-Registry.
	 * 
	 * @throws AccessException
	 * @throws RemoteException
	 * @throws NotBoundException
	 */
	protected void stopRegistry() throws AccessException, RemoteException, NotBoundException
	{
		registry.unbind(Configuration.BENCHMARK_NAME);
		UnicastRemoteObject.unexportObject(remoteLogger, true);
		UnicastRemoteObject.unexportObject(registry, true);
		UnicastRemoteObject.unexportObject(this, true);
	}

	/**
	 * Überprüft die Initialisierung der LoadDriver.
	 * 
	 * @return Wahrheitswert, der angibt, ob der Load Driver initialisiert ist
	 * @throws RemoteException
	 */
	public boolean isInitiated() throws RemoteException
	{
		if (loaddrivers.size() < config.getLoadDrivers())
		{
			return false;
		}
		for (LoadDriverIF loaddriver : loaddrivers)
		{
			if (loaddriver.getStage() != STAGE.READY)
			{
				return false;
			}
		}
		return true;
	}

	/**
	 * Registriert einen LoadDriver im Benchmark und gibt eine eindeutige ID für den LoadDriver zurück.
	 * 
	 * @param loadDriver zu registrierendes LoadDriver-Objekt
	 * @return ID des LoadDrivers
	 * @throws RemoteException
	 * @throws AlreadyBoundException
	 */
	@Override
	public synchronized int registerLoadDriver(LoadDriverIF loadDriver) throws RemoteException, AlreadyBoundException
	{
		if (loaddrivers.size() >= config.getLoadDrivers())
		{
			throw new AlreadyBoundException();
		}
		loaddrivers.add(loadDriver);
		return loaddrivers.size();
	}

	/**
	 * De-Registriert alle LoadDriver.
	 * 
	 * @throws RemoteException
	 * @throws SQLException
	 */
	protected void closeLoadDrivers() throws RemoteException, SQLException
	{
		for (LoadDriverIF loaddriver : loaddrivers)
		{
			loaddriver.close();
		}
	}

	/**
	 * Schließt den Benchmark.
	 * 
	 * @throws RemoteException
	 * @throws SQLException
	 * @throws NotBoundException
	 */
	protected void close() throws RemoteException, SQLException, NotBoundException
	{
		closeLoadDrivers();
		stopRegistry();
	}

	/**
	 * Stellt die genutzte Konfiguration zur Verfügung.
	 * 
	 * @return Configuration
	 * @throws RemoteException
	 */
	@Override
	public Configuration getConfiguration() throws RemoteException
	{
		return config;
	}

	/**
	 * Stellt den RemoteLogger zur Verfügung.
	 * 
	 * @return RemoteLoggerIF
	 * @throws RemoteException
	 */
	@Override
	public RemoteLoggerIF getRemoteLogger() throws RemoteException
	{
		return remoteLogger;
	}

	/**
	 * Führt den Benchmark aus und gibt das Ergebnis zurück.
	 * 
	 * @return Ergebnisobjekt mit den Benchmark-Ergebnissen
	 * @throws Exception
	 */
	public Result run() throws Exception
	{
		Result result = new Result(Configuration.BENCHMARK_NAME, config.getBenchmarkTime());
		ExecutorService es = Executors.newCachedThreadPool();
		ArrayList<Future<Result>> futures = new ArrayList<Future<Result>>();

		setStage(STAGE.WARMUP);
		log.info("Phase: WARMUP");

		for (LoadDriverIF loaddriver : loaddrivers)
		{
			futures.add(es.submit(loaddriver));
		}

		Thread.sleep(config.getWarmUpTime() * 1000);
		setStage(STAGE.BENCHMARK);
		log.info("Phase: BENCHMARK");
		Thread.sleep(config.getBenchmarkTime() * 1000);
		setStage(STAGE.COOLDOWN);
		log.info("Phase: COOLDOWN");
		Thread.sleep(config.getCoolDownTime() * 1000);
		setStage(STAGE.FINISHED);
		log.info("Phase: FINISHED");

		for (Future<Result> future : futures)
		{
			result.addSubResult(future.get());
		}
		log.info("Erfolgreiche Transaktionen: " + result.getSuccessfullTransactions());
		log.info("Fehlgeschlagene Transaktionen: " + result.getFailedTransactions());
		log.info("Durschnittsdauer einer Transaktion in ms: " + result.getAverageDuration());
		log.info("Transaktionen pro Sekunde: " + result.getTransactionsPerSecond());

		es.shutdown();
		close();

		return result;
	}

	/**
	 * Legt die aktuelle Phase fest.
	 * 
	 * @param stage Phase
	 * @throws RemoteException
	 */
	protected void setStage(STAGE stage) throws RemoteException
	{
		for (LoadDriverIF loaddriver : loaddrivers)
		{
			loaddriver.setStage(stage);
		}
	}

	/**
	 * Hauptprogramm zum Starten des Benchmarks.
	 * 
	 * @param args Aufrufparameter
	 */
	public static void main(String[] args)
	{
		try
		{
			Benchmark benchmark = new Benchmark();
			System.out.println("Der Benchmark Controller wurde gestartet.");
			System.out.println("Die Load Driver können jetzt auch gestartet werden!");
			do
			{
				Thread.sleep(1000);
			} while (!benchmark.isInitiated());
			System.out.println("Der Benchmark-Lauf wird gestartet.");
			Result result = benchmark.run();
			System.out.println("Der Benchmark-Lauf wurde ausgeführt.\n");
			printResult(result);
		} catch (Exception e)
		{
			e.printStackTrace();
			System.exit(0);
		}
	}

	protected static String tab = "";

	protected static void printResult(Result result)
	{
		System.out.printf("%sName: %s\n", tab, result.getName());
		System.out.printf("%sErfolgreiche Transaktionen: %d\n", tab, result.getSuccessfullTransactions());
		System.out.printf("%sFehlgeschlagene Transaktionen: %d\n", tab, result.getFailedTransactions());
		System.out.printf("%sTransaktionen pro Sekunde: %d\n", tab, result.getTransactionsPerSecond());
		System.out.printf("%sDurchschnittsdauer einer Transaktion: %f ms\n", tab, result.getAverageDuration());
		tab += "\t";
		for (Result subResult : result.getSubResults())
		{
			printResult(subResult);
		}
		tab = tab.substring(1);
	}

}
