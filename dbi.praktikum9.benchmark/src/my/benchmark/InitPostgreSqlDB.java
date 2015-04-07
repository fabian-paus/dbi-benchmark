package my.benchmark;

import dbi.benchmark.Benchmarker;

public class InitPostgreSqlDB {
	public static void main(String[] args) {	
		try {
			Benchmarker benchmarker = null;
			ConfigurationAdapter config = new ConfigurationAdapter();
			
			try {
				benchmarker = new Benchmarker(config);
				benchmarker.execute();
				
				System.out.println("Initialisierung: " 
						+ benchmarker.getElapsedTime() + "ms");
			} finally {
				if (benchmarker != null)
					benchmarker.close();
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
}
