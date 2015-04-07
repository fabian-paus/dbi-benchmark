package de.whs.dbi.util;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Ein Result-Objekt kapselt wichtige Benchmark-Ergebnisse. Ggf. ist es hierarchisch aufgebaut
 * und fasst dann mehrere Teilergebnisse zusammen.
 */
public class Result implements Serializable
{
	private static final long serialVersionUID = 465986949533827471L;

	private String name;
	private int successfullTransactions = 0;
	private int failedTransactions = 0;
	private long duration = 0;
	private int benchmarkTime;
	private ArrayList<Result> subResults = new ArrayList<Result>();

	/**
	 * Der Konstruktor initialisiert das Ergebnis.
	 * 
	 * @param name Name des Ergebnisses
	 * @param benchmarkTime Dauer der Benchmark-Phase in ms
	 */
	public Result(String name, int benchmarkTime)
	{
		this.name = name;
		this.benchmarkTime = benchmarkTime;
	}

	/**
	 * Gibt den Namen des Ergebnisses zur�ck.
	 * 
	 * @return Name
	 */
	public String getName()
	{
		return name;
	}

	/**
	 * Gibt die gesamte Dauer der erfolgreichen Transaktionen in ms zur�ck.
	 * 
	 * @return Dauer
	 */
	public long getDuration()
	{
		return duration;
	}

	/**
	 * Gibt die Anzahl der erfolgreichen Transaktionen zur�ck.
	 * 
	 * @return Anzahl
	 */
	public int getSuccessfullTransactions()
	{
		return successfullTransactions;
	}

	/**
	 * Erh�ht die Anzahl der erfolgreichen Transaktionen.
	 * 
	 * @param duration Dauer
	 */
	public void incSuccessfullTransactions(long duration)
	{
		successfullTransactions++;
		this.duration += duration;
	}

	/**
	 * Gibt die Anzahl der fehlgeschlagenen Transaktionen zur�ck.
	 * 
	 * @return Anzahl
	 */
	public int getFailedTransactions()
	{
		return failedTransactions;
	}

	/**
	 * Erh�ht die Anzahl der fehlgeschlagenen Transaktionen.
	 */
	public void incFailedTransactions()
	{
		failedTransactions++;
	}

	/**
	 * Gibt die zusammengefasste Ergebnisse zur�ck.
	 * 
	 * @return Ergebnisse
	 */
	public ArrayList<Result> getSubResults()
	{
		return subResults;
	}

	/**
	 * F�gt ein Ergebnis hinzu.
	 * 
	 * @param subResult Ergebnis
	 */
	public void addSubResult(Result subResult)
	{
		subResults.add(subResult);
		successfullTransactions += subResult.getSuccessfullTransactions();
		failedTransactions += subResult.getFailedTransactions();
		duration += subResult.getDuration();
	}

	/**
	 * Gibt die Anzahl der Transaktionen pro Sekunde (TPS) zur�ck.
	 * 
	 * @return TPS
	 */
	public int getTransactionsPerSecond()
	{
		if (benchmarkTime <= 0)
			return 0;
		return (int) ((double) successfullTransactions / (double) benchmarkTime);
	}

	/**
	 * Gibt die durchschnittliche Dauer einer Transaktion in ms zur�ck.
	 * 
	 * @return Dauer
	 */
	public double getAverageDuration()
	{
		if (successfullTransactions <= 0)
			return 0;
		return (double) duration / (double) successfullTransactions;
	}

}
