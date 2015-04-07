package my.benchmark;

import java.io.IOException;

/**
 * Diese Klasse ist ein Adapter, der die Klasse de.whs.dbi.util.Configuration
 * kompatibel zur Klasse dbi.benchmark.Configuration macht. Dies wird benötigt,
 * um die Datenbankanlage aus dem vorherigen Benchmark wieder zu verwenden.
 *
 */
public class ConfigurationAdapter extends dbi.benchmark.Configuration {
	
	private de.whs.dbi.util.Configuration base;

	public ConfigurationAdapter() throws IOException {
		base = new de.whs.dbi.util.Configuration();
		base.loadBenchmarkConfiguration();
	}
	
	@Override
	public String getJdbcUrl() {
		return base.getDatabaseJDBC();
	}
	
	@Override
	public String getJdbcUser() {
		return base.getUser("database.username");
	}
	
	@Override
	public String getJdbcPassword() {
		return base.getUser("database.password");
	}
	
	@Override
	public int getScaleN() {
		return Integer.parseInt(base.getUser("n"));
	}
	
	@Override
	public int getInsertMethod() {
		return 6;
	}

}
