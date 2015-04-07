package de.whs.dbi.loaddriver;

import java.lang.reflect.InvocationTargetException;
import java.rmi.NoSuchObjectException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

import de.whs.dbi.benchmark.BenchmarkIF;
import de.whs.dbi.benchmark.BenchmarkIF.STAGE;
import de.whs.dbi.util.Configuration;
import de.whs.dbi.util.RemoteHandler;
import de.whs.dbi.util.Result;

/**
 * LoadDriver realisiert einen einzelnen Load Driver.
 */
public class LoadDriver extends UnicastRemoteObject implements LoadDriverIF
{
	private static final long serialVersionUID = -3045240234269310354L;

	protected Registry registry;

	protected Integer loadDriverID;
	protected Configuration config;
	protected BenchmarkIF benchmark;
	protected Logger log;
	protected Database database;

	protected ArrayList<Transaction> transactions = new ArrayList<Transaction>();
	protected ArrayList<Integer> weights = new ArrayList<Integer>();

	protected STAGE stage = STAGE.INIT;

	/**
	 * Der Konstruktor initialisiert den LoadDriver, sodass dieser im Benchmark
	 * verfügbar ist.
	 * 
	 * @throws Exception
	 */
	public LoadDriver() throws Exception
	{
		config = new Configuration();
		config.loadRMIConfiguration();

		registry = LocateRegistry.getRegistry(config.getRegistryHost(), config.getRegistryPort());
		benchmark = (BenchmarkIF) registry.lookup(Configuration.BENCHMARK_NAME);

		config = benchmark.getConfiguration();

		loadDatabase();
		loadTransactions();

		loadDriverID = benchmark.registerLoadDriver(this);

		log = Logger.getLogger(LoadDriver.class.getCanonicalName() + loadDriverID);
		log.addHandler(new RemoteHandler(benchmark.getRemoteLogger()));
		log.setLevel(config.getLogLevel());

		stage = STAGE.READY;
	}

	/**
	 * Initialisiert die Datenbank mitsamt einer Verbindung.
	 * 
	 * @throws ClassNotFoundException
	 * @throws NoSuchMethodException
	 * @throws SecurityException
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 * @throws SQLException
	 * @throws IllegalArgumentException
	 * @throws InvocationTargetException
	 */
	protected void loadDatabase() throws ClassNotFoundException, NoSuchMethodException, SecurityException, InstantiationException,
			IllegalAccessException, SQLException, IllegalArgumentException, InvocationTargetException
	{
		database = (Database) Class.forName(config.getDatabaseClass()).getConstructor(Configuration.class).newInstance(config);
	}

	/**
	 * Schließt die Datenbankverbindung.
	 * 
	 * @throws SQLException
	 */
	protected void closeDatabase() throws SQLException
	{
		database.closeConnection();
	}

	/**
	 * Initialisiert die Transaktionen mit den dazugehörigen Gewichtungen.
	 * 
	 * @throws NoSuchMethodException
	 * @throws SecurityException
	 */
	protected void loadTransactions() throws NoSuchMethodException, SecurityException
	{
		Integer weight = 0;
		for (String transactionName : config.getTransactions())
		{
			transactions.add(database.createTransaction(transactionName));
			weight += config.getTransactionWeight(transactionName);
			weights.add(weight);
		}
	}

	/**
	 * Wählt eine Transaktion unter der Berücksichtigung der Gewichtungen aus.
	 * 
	 * @return Ausgewählte Transaktion
	 * @throws NoSuchMethodException
	 * @throws SecurityException
	 */
	protected Transaction chooseTransaction() throws NoSuchMethodException, SecurityException
	{
		int i;
		Integer randomWeight = ParameterGenerator.generateRandomInt(1, weights.get(weights.size() - 1));

		for (i = 0; i < transactions.size(); i++)
		{
			Integer weight = weights.get(i);
			if (randomWeight <= weight)
				break;
		}

		return transactions.get(i);
	}

	/**
	 * De-Registriert den LoadDriver.
	 * 
	 * @throws SQLException
	 * @throws NoSuchObjectException
	 */
	public void close() throws SQLException, NoSuchObjectException
	{
		closeDatabase();
		UnicastRemoteObject.unexportObject(this, true);
	}

	/**
	 * Führt den Benchmark aus und gibt das Ergebnis zurück.
	 * 
	 * @return Ergebnis
	 * @throws Exception
	 */
	@Override
	public Result call() throws Exception
	{
		while (stage != STAGE.FINISHED)
		{
			// Ausführung der Transaktion
			
			Transaction	transaction = chooseTransaction();
			try
			{
				long duration = System.currentTimeMillis();
				// Ausführen der Transaktion in eigener Methode, um 
				// Serialisierungsfehler abzufangen
				executeTransaction(transaction);
				duration = System.currentTimeMillis() - duration;
				log.log(Level.FINE, "Dauer der erfolgreichen Transaktion \"" + transaction.getName() + "\" in ms: " + duration);
				if (stage == STAGE.BENCHMARK)
					transaction.getResult().incSuccessfullTransactions(duration);
				Thread.sleep(config.getThinkTime());
			}
			catch (Exception e)
			{
				log.log(Level.WARNING, "Fehlgeschlagene Transaktion \""
						+ transaction.getName() + "\"", e.getCause());
				if (stage == STAGE.BENCHMARK)
					transaction.getResult().incFailedTransactions();
			}
		}

		// Zusammenfassen der Ergebnisse
		Result result = new Result(Configuration.LOADDRIVER_NAME + loadDriverID, config.getBenchmarkTime());
		for (Transaction transaction : transactions)
		{
			Result subResult = transaction.getResult();
			result.addSubResult(subResult);
			log.finer("Erfolgreiche Transaktionen \"" + subResult.getName() + "\": " + subResult.getSuccessfullTransactions());
			log.finer("Fehlgeschlagene Transaktionen \"" + subResult.getName() + "\": " + subResult.getFailedTransactions());
			log.finer("Durschnittsdauer einer Transaktion in ms \"" + subResult.getName() + "\": " + subResult.getAverageDuration());
			log.finer("Transaktionen pro Sekunde \"" + subResult.getName() + "\": " + subResult.getTransactionsPerSecond());
		}
		log.fine("Erfolgreiche Transaktionen: " + result.getSuccessfullTransactions());
		log.fine("Fehlgeschlagene Transaktionen: " + result.getFailedTransactions());
		log.fine("Durschnittsdauer einer Transaktion in ms: " + result.getAverageDuration());
		log.fine("Transaktionen pro Sekunde: " + result.getTransactionsPerSecond());

		return result;
	}

	/**
	 * Diese Methode versucht eine Transaktion solange auszuführen, bis diese
	 * vom DBMS serialisiert ausgeführt werden konnte.
	 * 
	 * Das ist notwendig, weil PostgreSQL eine Implementierung des Isolationslevels
	 * SERIALIZABLE verwendet, die bei einem Serialisierungsfehler ein manuelles
	 * Neustarten der Transaktion erfordert. Bei einem Serialisierungsfehler zwischen
	 * zwei Transaktionen wird eine abgebrochen (mit SQLState 40001) und die andere
	 * wird abgeschlossen. Der Serialisierungsfehler wird erst gemeldet, wenn die
	 * andere Transaktion beendet wurde. Dadurch wird vermieden, dass derselbe Fehler
	 * bei Neustart wieder auftritt.
	 * 
	 * @param transaction Transaktion, die ausgeführt werden soll.
	 * @throws Exception Im Fehlerfall.
	 */
	private void executeTransaction(Transaction transaction) throws Exception {
		boolean serialized = false;
		while (!serialized) {
			serialized = tryExecute(transaction);
		}
	}

	/**
	 * Versucht eine Transaktion auszuführen.
	 * 
	 * @param transaction Transaktion, die ausgeführt werden soll.
	 * @return true, wenn die Transaktion erfolgreich ausgeführt wurde.
	 *         false, wenn ein Serialisierungsfehler aufgetreten ist.
	 * @throws Exception Bei allen anderen Fehlern.
	 */
	private boolean tryExecute(Transaction transaction) throws Exception {
		final String SERIALIZATION_FAILURE = "40001";
		
		try {
			transaction.execute();
			return true;
		}
		catch (SQLException e) {
			if (e.getSQLState().equals(SERIALIZATION_FAILURE))
				return false;
			else
				throw e;
		}
		catch (InvocationTargetException e) {
			// Durch RMI kann die SQLException in einer InvocationTargetException
			// verpackt sein
			Throwable target = e.getTargetException();
			if (target instanceof SQLException) {
				SQLException s = (SQLException)target;
				if (s.getSQLState().equals(SERIALIZATION_FAILURE))
					return false;
				else
					throw e;
			} else
				throw e;
		}
	}

	/**
	 * Gibt die aktuelle Phase zurück.
	 * 
	 * @return Phase
	 * @throws RemoteException
	 */
	@Override
	public STAGE getStage() throws RemoteException
	{
		return stage;
	}

	/**
	 * Legt die aktuelle Phase fest.
	 * 
	 * @param stage Phase
	 * @throws RemoteException
	 */
	@Override
	public void setStage(STAGE stage) throws RemoteException
	{
		this.stage = stage;
	}

	/**
	 * Hauptprogramm zum Starten eines LoadDrivers.
	 * 
	 * @param args übergebene Aufrufparameter
	 */
	public static void main(String[] args)
	{
		try
		{
			new LoadDriver();
		} catch (Exception e)
		{
			e.printStackTrace();
			System.exit(0);
		}
	}

}
