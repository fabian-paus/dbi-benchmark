package de.whs.dbi.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.util.InvalidPropertiesFormatException;
import java.util.Properties;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Eine Konfiguration beinhaltet alle variablen und einstellbaren Eigenschaften
 * zu einem Benchmark-Lauf. Konfigurationen sind serialisierbar und können daher
 * per RMI als Aufrufparameter übertragen werden.
 */
public class Configuration implements Serializable
{
	private static final long serialVersionUID = -4491234893133092327L;
	
	/**
	 * XML-Konfigurationsdatei mit allgemeinen Benchmarkeinstellungen
	 */
	protected final static String BENCHMARK_FILE = "Benchmark.xml";

	/**
	 * XML-Konfigurationsdatei zum Lastprofil
	 */
	protected final static String TRANSACTIONS_FILE = "Transactions.xml";

	/**
	 * XML-Konfigurationsdatei zur RMI-Registry
	 */
	protected final static String RMI_FILE = "RMI.xml";
	
	/**
	 * Bezeichnung für einen LoadDriver
	 */
	public final static String LOADDRIVER_NAME = "LoadDriver";
	
	/**
	 * Bezeichnung für einen Benchmark
	 */
	public final static String BENCHMARK_NAME = "Benchmark";
	
	// Properties der Konfigurationen
	protected Properties pBenchmark;
	protected Properties pTransactions;
	protected Properties pRMI;
	
	/**
	 * Standard Konfiguration für einen Benchmark
	 */
	protected final static Properties pBenchmarkDefaults;
	
	/**
	 * Initialisiert die Standard-Konfiguration für einen Benchmark.
	 */
	static 
	{
		pBenchmarkDefaults = new Properties();
		pBenchmarkDefaults.setProperty("database.jdbc", "");
		pBenchmarkDefaults.setProperty("database.class", "de.whs.dbi.loaddriver.MyTransactionsForMyDBMS");
		pBenchmarkDefaults.setProperty("loaddrivers", "0");		
		pBenchmarkDefaults.setProperty("log.level", "CONFIG");
		pBenchmarkDefaults.setProperty("warmup.time", "0");
		pBenchmarkDefaults.setProperty("benchmark.time", "0");
		pBenchmarkDefaults.setProperty("cooldown.time", "0");
		pBenchmarkDefaults.setProperty("thinktime", "0");		
	}
	
	/**
	 * Der Konstruktor initialisiert die Konfiguration.
	 * 
	 * @throws InvalidPropertiesFormatException
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	public Configuration() throws InvalidPropertiesFormatException, FileNotFoundException, IOException 
	{
		pBenchmark = new Properties(pBenchmarkDefaults);
		pTransactions = new Properties();	
		pRMI = new Properties();
	}
	
	/**
	 * Lädt die Konfiguration für den Benchmark.
	 * 
	 * @throws InvalidPropertiesFormatException
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	public void loadBenchmarkConfiguration() throws InvalidPropertiesFormatException, FileNotFoundException, IOException 
	{
		pBenchmark.loadFromXML(new FileInputStream(new File(BENCHMARK_FILE)));		
	}
	
	/**
	 * Lädt die Konfigruation für die Transaktionen.
	 * 
	 * @throws InvalidPropertiesFormatException
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	public void loadTransactionsConfiguration() throws InvalidPropertiesFormatException, FileNotFoundException, IOException 
	{
		pTransactions.loadFromXML(new FileInputStream(new File(TRANSACTIONS_FILE)));	
	}
	
	/**
	 * Loggt die Konfiguration für den Benchmark.
	 * 
	 * @param log
	 */
	public void logBenchmarkConfiguration(Logger log)
	{
		for (String key : pBenchmark.stringPropertyNames()) 
		{
			log.config(key + " = " + pBenchmark.getProperty(key));
		}
	}
	
	/**
	 * Loggt die Konfiguration für die Transaktionen.
	 * 
	 * @param log
	 */
	public void logTransactionConfiguration(Logger log)
	{
		for (String key : pTransactions.stringPropertyNames()) 
		{
			log.config("Transaktion " + key + " " + pTransactions.getProperty(key));
		}
	}
	
	/**
	 * Lädt die Konfiguration für die RMI-Registry.
	 * 
	 * @throws InvalidPropertiesFormatException
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	public void loadRMIConfiguration() throws InvalidPropertiesFormatException, FileNotFoundException, IOException 
	{
		pRMI.loadFromXML(new FileInputStream(new File(RMI_FILE)));
	}
	
	/**
	 * Speichert die Konfiguration für den Benchmark.
	 * 
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	public void saveBenchmarkConfiguration() throws FileNotFoundException, IOException 
	{
		pBenchmark.storeToXML(new FileOutputStream(new File(BENCHMARK_FILE)), "Benchmark");
	}
	
	/**
	 * Speichert die Konfiguration für die Transaktionen.
	 * 
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	public void saveTransactionsConfiguration() throws FileNotFoundException, IOException 
	{
		pTransactions.storeToXML(new FileOutputStream(new File(TRANSACTIONS_FILE)), "Transactions");
	}
	
	/**
	 * Speichert die Konfiguration für die RMI-Registry.
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	public void saveRMIConfiguration() throws FileNotFoundException, IOException 
	{
		pRMI.storeToXML(new FileOutputStream(new File(RMI_FILE)), "RMI");
	}
	
	/**
	 * Gibt den Port der RMI-Registry zurück.
	 * 
	 * @return Port
	 */
	public Integer getRegistryPort() 
	{		
		return Integer.parseInt(pRMI.getProperty("registry.port"));
	}
	
	/**
	 * Legt den Port für die RMI-Registry fest.
	 * 
	 * @param port Port
	 */
	public void setRegistryPort(int port) 
	{
		pRMI.setProperty("registry.port", Integer.toString(port));
	}
	
	/**
	 * Gibt den Host/IP der RMI-Registry zurück.
	 * 
	 * @return Host/IP
	 */
	public String getRegistryHost() 
	{
		return pRMI.getProperty("registry.host");
	}
	
	/**
	 * Legt den Host/IP der RMI-Registry fest.
	 * 
	 * @param host Host/IP
	 */
	public void setRegistryHost(String host) 
	{
		pRMI.setProperty("registry.host", host);
	}
	
	/**
	 * Gibt den JDBC-Verbindungsstring zurück.
	 * 
	 * @return JDBC-URL
	 */
	public String getDatabaseJDBC() 
	{
		return pBenchmark.getProperty("database.jdbc");
	}
	
	/**
	 * Legt den JDBC-Verbindungsstring fest.
	 * 
	 * @param jdbc JDBC-URL
	 */
	public void setDatabaseJDBC(String jdbc) 
	{
		pBenchmark.setProperty("registry.port", jdbc);
	}
	
	/**
	 * Gibt die Datenbankklasse zurück.
	 * 
	 * @return Datenbankklasse
	 */
	public String getDatabaseClass() 
	{
		return pBenchmark.getProperty("database.class");
	}
	
	/**
	 * Legt die Datenbankklasse fest.
	 * 
	 * @param databaseClass Datenbankklasse
	 */
	public void setDatabaseClass(String databaseClass) 
	{
		pBenchmark.setProperty("registry.port", databaseClass);
	}
	
	/**
	 * Gibt die Anzahl der LoadDriver zurück.
	 * 
	 * @return Anzahl der LoadDriver
	 */
	public int getLoadDrivers() 
	{
		return Integer.parseInt(pBenchmark.getProperty("loaddrivers"));
	}
	
	/**
	 * Legt die Anzahl der LoadDriver fest.
	 * 
	 * @param n Anzahl
	 */
	public void setLoadDrivers(int n) 
	{
		pBenchmark.setProperty("registry.port", Integer.toString(n));
	}
	
	/**
	 * Gibt das Log-Level zurück.
	 * 
	 * @return Log-Level
	 */
	public Level getLogLevel() 
	{
		return Level.parse(pBenchmark.getProperty("log.level"));
	}
	
	/**
	 * Legt das Log-Level fest.
	 * 
	 * @param level Log-Level
	 */
	public void setLogLevel(Level level) 
	{
		pBenchmark.setProperty("registry.port", level.getName());
	}
	
	/**
	 * Gibt alle Transaktionen zurück.
	 * 
	 * @return Transaktionen
	 */
	public Set<String> getTransactions() 
	{
		return pTransactions.stringPropertyNames();
	}
	
	/**
	 * Gibt die Gewichtung einer Transaktion zurück.
	 * 
	 * @param transaction Transaktion
	 * @return Gewichtung
	 */
	public Integer getTransactionWeight(String transaction) 
	{
		int i = Integer.parseInt(pTransactions.getProperty(transaction));
		if (i>0)
			return i;
		return 0;
	}
	
	/**
	 * Fügt eine Transaktion hinzu.
	 * 
	 * @param transaction Transaktion
	 * @param weight Gewichtung
	 */
	public void addTransaction(String transaction, int weight) 
	{
		pTransactions.setProperty(transaction, Integer.toString(weight));
	}
	
	/**
	 * Entfernt eine Transaktion.
	 * 
	 * @param transaction Transaktion
	 */
	public void removeTransaction(String transaction) 
	{		
		if (pTransactions.containsKey(transaction))
			pTransactions.remove(transaction);
	}
	
	/**
	 * Gibt die Zeitdauer der Phase WARMUP in ms zurück.
	 * 
	 * @return Zeitdauer
	 */
	public int getWarmUpTime() 
	{
		int i = Integer.parseInt(pBenchmark.getProperty("warmup.time"));
		if (i >= 0)
			return i;
		return Integer.parseInt(pBenchmarkDefaults.getProperty("warmup.time"));
	}
	
	/**
	 * Gibt die Zeitdauer der Phase BENCHMARK  in ms zurück.
	 * 
	 * @return Zeitdauer
	 */
	public int getBenchmarkTime() 
	{
		Integer i = Integer.parseInt(pBenchmark.getProperty("benchmark.time"));
		if (i >= 0)
			return i;
		return Integer.parseInt(pBenchmarkDefaults.getProperty("benchmark.time"));
	}
	
	/**
	 * Gibt die Zeitdauer der Phase COOLDOWN in ms zurück.
	 * 
	 * @return Zeitdauer
	 */
	public int getCoolDownTime() 
	{
		Integer i = Integer.parseInt(pBenchmark.getProperty("cooldown.time"));
		if (i >= 0)
			return i;
		return Integer.parseInt(pBenchmarkDefaults.getProperty("cooldown.time"));
	}
	
	/**
	 * Legt die Zeitdauer der Phase WARMUP in ms fest.
	 * 
	 * @param time Zeitdauer
	 */
	public void setWarmUpTime(int time) 
	{
		if (time < 0)
			time = Integer.parseInt(pBenchmarkDefaults.getProperty("warmup.time"));
		pBenchmark.setProperty("warmup.time", Integer.toString(time));
	}
	
	/**
	 * Legt die Zeitdauer der Phase BENCHMARK in ms fest.
	 * 
	 * @param time Zeitdauer
	 */
	public void setBenchmarkTime(int time) 
	{
		if (time < 0)
			time = Integer.parseInt(pBenchmarkDefaults.getProperty("benchmark.time"));
		pBenchmark.setProperty("benchmark.time", Integer.toString(time));
	}
	
	/**
	 * Legt die Zeitdauer der Phase COOLDOWN in ms fest.
	 * 
	 * @param time Zeitdauer
	 */
	public void setCoolDownTime(int time)
	{
		if (time < 0)
			time = Integer.parseInt(pBenchmarkDefaults.getProperty("cooldown.time"));
		pBenchmark.setProperty("cooldown.time", Integer.toString(time));
	}
	
	/**
	 * Gibt die ThinkTime in ms zurück.
	 * 
	 * @return Zeitdauer
	 */
	public int getThinkTime() 
	{
		int i = Integer.parseInt(pBenchmark.getProperty("thinktime"));
		if (i >= 0)
			return i;
		return Integer.parseInt(pBenchmarkDefaults.getProperty("thinktime"));
	}
	
	/**
	 * Legt die ThinkTime fest.
	 * 
	 * @param time Zeitdauer
	 */
	public void setThinkTime(int time) 
	{
		if (time < 0)
			time = Integer.parseInt(pBenchmarkDefaults.getProperty("thinktime"));
		pBenchmark.setProperty("thinktime", Integer.toString(time));
	}
	
	/**
	 * Gibt einen benutzerdefinierten Konfigurationsparameter zurück.
	 * 
	 * @param key Parameterschlüssel
	 * @return Parameterwert
	 */
	public String getUser(String key)
	{
		return pBenchmark.getProperty("user." + key);
	}
	
	/**
	 * Legt einen benutzerdefinierten Konfigurationsparameter fest.
	 * 
	 * @param key Parameterschlüssel
	 * @param value Parameterwert
	 */
	public void setUser(String key, String value)
	{
		pBenchmark.setProperty("user." + key, value);
	}
	
}
