package dbi.benchmark;

import java.io.*;
import java.util.Properties;


/**
 * Diese Klasse kapselt das Laden und Auslesen der zentralen
 * Konfigurationsdatei.
 *
 */
public class Configuration {
	private static final String CONFIGURATION_FILE = "dbi.benchmark.properties";
	
	private String jdbcUrl;
	private String jdbcUser;
	private String jdbcPassword;
	private int scaleN;
	private int insertMethod;
	
	/**
	 * Legt ein Objekt an ohne die Konfigurationsdatei zu laden.
	 */
	protected Configuration() {
		
	}
	
	/**
	 * Lädt die Konfiguration aus der zentralen Datei.
	 * 
	 * @throws IOException Wenn ein Fehler beim Lesen der Datei auftritt.
	 */
	public static Configuration load() throws IOException {
		Configuration config = new Configuration();
		
		BufferedInputStream stream = new BufferedInputStream(
				new FileInputStream(CONFIGURATION_FILE));
		try {
			Properties properties = new Properties();
			properties.load(stream);
			
			config.jdbcUrl = properties.getProperty("JDBC_URL");
			config.jdbcUser = properties.getProperty("JDBC_USER");
			config.jdbcPassword = properties.getProperty("JDBC_PASSWORD");
			
			String strScaleN = properties.getProperty("SCALE_N");
			String strInsertMethod = properties.getProperty("INSERT_METHOD");
			config.scaleN = Integer.parseInt(strScaleN);
			config.insertMethod = Integer.parseInt(strInsertMethod);
		}
		finally {
			stream.close();
		}
		
		return config;
	}
	
	/**
	 * Liefert die URL für die JDBC-Verbindung.
	 * 
	 * @return JDBC URL.
	 */
	public String getJdbcUrl() {
		return jdbcUrl;
	}
	
	/**
	 * Liefert den Datenbankbenutzer für die JDBC-Verbindung.
	 * 
	 * @return JDBC Benutzer.
	 */
	public String getJdbcUser() {
		return jdbcUser;
	}
	
	/**
	 * Liefert das Passwort des Datenbankbenutzers für die JDBC-Verbindung.
	 * 
	 * @return JDBC Passwort.
	 */
	public String getJdbcPassword() {
		return jdbcPassword;
	}
	
	/**
	 * Liefert den Skalierungsfaktor n.
	 * 
	 * @return Skalierungsfaktor n.
	 */
	public int getScaleN() {
		return scaleN;
	}
	
	/**
	 * Liefert die ID der zu verwendenden Einfügemethode.
	 * 
	 * @return ID der Einfügemethode.
	 */
	public int getInsertMethod() {
		return insertMethod;
	}
}
