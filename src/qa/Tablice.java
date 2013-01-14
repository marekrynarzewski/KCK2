package qa;

import java.util.Vector;

public class Tablice
{
	public static boolean saPuste(String[] tablica) throws NullPointerException
	{
		for (String element : tablica)
		{
			if (element != null)
			{
				if (element.equals(""))
				{
					return true;
				}
			}
			else
			{
				throw new NullPointerException();
			}
		}
		return false;
	}
	
	/**
	 * znajduje wszystkie indeksy w tablicy które równają się item
	 * @param array - tablica
	 * @param item - szukana wartość
	 * @return wektor indeksów
	 */
	public static <T> Vector<Integer> znajdzWszystkie(T[] tablica, T element)
	{
		Vector<Integer> rezultat = new Vector<Integer>();
		int idx = 0;
		for (T innyElement : tablica)
		{
			if (innyElement.equals(element))
			{
				rezultat.add(new Integer(idx));
			}
			++idx;
		}
		return rezultat;
	}

	public static <T> Vector<T> doWektora(T[] tablica)
	{
		Vector<T> rezultat = new Vector<T>();
		for (T element : tablica)
		{
			rezultat.add(element);
		}
		return rezultat;
	}
	
	public static <T> boolean zawiera(T[] tablica, T element)
	{
		Vector<Integer> wektor = znajdzWszystkie(tablica, element);
		return wektor.size() > 0;
	}
}
