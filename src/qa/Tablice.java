package qa;

import java.lang.reflect.Array;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.Vector;

public class Tablice
{
	/**
	 * znajduje wszystkie indeksy w tablicy które równają się elementowi
	 * @param tablica - tablica
	 * @param element - szukana wartość
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
	
	public static <T> Vector<T> podWektor(Vector<T> wektor, int pozycjaOd, int pozycjaDo)
	{
		Vector<T> rezultat = new Vector<T>();
		int pozycja = 0;
		for (T klucz : wektor)
		{
			if (pozycja >= pozycjaOd && pozycja <= pozycjaDo)
			{
				rezultat.add(klucz);
			}
			++pozycja;
		}
		return rezultat;
	}

	public static <K, V> long pozycjaKluczaWMapie(Map<K, V> zrodlo, K klucz)
	{
		long wynik = -1;
		if (!zrodlo.containsKey(klucz))
		{
			return wynik;
		}
		else
		{
			++wynik;
			for (Map.Entry<K, V> wpis : zrodlo.entrySet())
			{
				if (!wpis.getKey().equals(klucz))
				{
					++wynik;
				}
				else
				{
					++wynik;
					break;
				}
			}
			return wynik;
		}
	}

	public static <K, V> K zwrocKlucz(Map<K, V> zrodlo, long pozycja)
	{
		long wskaznik = 0;
		for (Map.Entry<K, V> wpis : zrodlo.entrySet())
		{
			if (wskaznik == pozycja)
			{
				return wpis.getKey();
			}
			else
			{
				++wskaznik;
			}
		}
		return null;
	}

	public static <K, V> SortedMap<K, V> goraMapy(SortedMap<K, V> zrodlo, long pozycji)
	{
		K toKey = zwrocKlucz(zrodlo, pozycji);
		if (toKey == null)
		{
			return new TreeMap<K, V>();
		}
		else
		{
			return zrodlo.headMap(toKey);
		}
	}
	
	public static <T> T[] resize(T[] oldArray, int newSize)
	{
        int oldSize = Array.getLength(oldArray);
        Class<?> elementType = oldArray.getClass().getComponentType();
        Object newArray = Array.newInstance(elementType, newSize);
        int preserveLength = Math.min(oldSize, newSize);
        if (preserveLength > 0) {
            System.arraycopy(oldArray, 0, newArray, 0, preserveLength);
        }
        return (T[]) newArray;
    }
	
	public static boolean zawieraja(String[] tablica, String element)
	{
		for (String obecny : tablica)
		{
			if (element.contains(obecny))
			{
				return true;
			}
		}
		return false;
	}
}
