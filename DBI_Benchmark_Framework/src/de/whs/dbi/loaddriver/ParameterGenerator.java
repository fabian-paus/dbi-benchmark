package de.whs.dbi.loaddriver;

import java.util.Random;

/**
 * ParameterGenerator enthält Hilfsmethoden zur Generierung von Zufallseingabeparametern
 * für unterschiedliche Datentypen.
 */
public class ParameterGenerator
{

	/**
	 * Random number generator
	 */
	protected static Random random = new Random();

	/**
	 * Zeichen, die bei der Generierung von zufälligen Strings verwendet werden
	 */
	protected static final char[] chars = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789".toCharArray();

	/**
	 * Gibt den den Random number generator zurück.
	 * 
	 * @return Random number generator
	 */
	public static Random getRandom()
	{
		return random;
	}

	/**
	 * Generiert eine zufällige Zahl in einem beliebigen Zahlenbereich.
	 * 
	 * @param min Untergrenze des Bereichs
	 * @param max Obergrenze des Bereichs
	 * @return Zufällige Zahl
	 */
	public static int generateRandomInt(int min, int max)
	{
		if (min > max)
		{
			throw new IllegalArgumentException();
		}

		return min + random.nextInt(max - min + 1);
	}

	/**
	 * Generiert einen zufälligen String einer beliebigen Länge.
	 * 
	 * @param length Länge des Strings
	 * @return Zufälliger String
	 */
	public static String generateRandomString(int length)
	{
		if (length < 1)
		{
			throw new IllegalArgumentException();
		}

		char str[] = new char[length];
		for (int i = 0; i < length; i++)
		{
			str[i] = chars[ParameterGenerator.generateRandomInt(0, chars.length - 1)];
		}
		return new String(str);
	}

}
