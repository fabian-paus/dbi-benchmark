package de.whs.dbi.loaddriver;

import java.lang.reflect.Method;

import de.whs.dbi.util.Result;

/**
 * Die Klasse Transaction kapselt im Framework eine einzelne Transaktion
 * aus dem genutzten Lastprofil, die als Methode einer Unterklasse von Database
 * realisiert wird.
 */
public class Transaction
{
	protected Database database;
	protected Method method;
	protected Result result;

	/**
	 * Der Konstruktor initialisiert eine Transaktion.
	 * 
	 * @param database Datenbank
	 * @param method Methode der Transaktion
	 * @param benchmarkTime Dauer der Benchmark-Phase in ms
	 */
	public Transaction(Database database, Method method, int benchmarkTime)
	{
		this.database = database;
		this.method = method;
		this.result = new Result(getName(), benchmarkTime);
	}

	/**
	 * Führt die Transaktion aus.
	 * 
	 * @throws Exception
	 */
	public void execute() throws Exception
	{
		database.beginTransaction();

		try
		{
			method.invoke(database);
		} catch (Exception e)
		{
			database.rollbackTransaction();
			throw e;
		}

		database.commitTransaction();
	}

	/**
	 * Gibt den Namen der Transaktion zurück.
	 * 
	 * @return Name
	 */
	public String getName()
	{
		return method.getName();
	}

	/**
	 * Gibt das Ergebnis der Transaktion zurück.
	 * 
	 * @return Ergebnis
	 */
	public Result getResult()
	{
		return result;
	}

}
