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
			if (element.equals(innyElement))
			{
				rezultat.add(new Integer(idx));
			}
			++idx;
		}
		return rezultat;
	}

	/**
	 * tablice standardowa zamienia na wektor
	 * @param tablica wejście
	 * @return wektor
	 */
	public static <T> Vector<T> doWektora(T[] tablica)
	{
		Vector<T> rezultat = new Vector<T>();
		for (T element : tablica)
		{
			rezultat.add(element);
		}
		return rezultat;
	}
	
	/**
	 * sprawdza czy w tablicy istnieje chociaż jeden element równy elementowi
	 * @param tablica standardowa
	 * @param element do wyszukania
	 * @return prawdę jeżeli znaleziono lub fałsz w przeciwnym razie
	 */
	public static <T> boolean zawiera(T[] tablica, T element)
	{
		Vector<Integer> wektor = znajdzWszystkie(tablica, element);
		return wektor.size() > 0;
	}
	
	/**
	 * zwraca jako który klucz wystąpił w mapie źródło
	 * @param zrodlo mapa
	 * @param klucz klucz zgodny typem kluczów mapy
	 * @return indeks >= 0 dla znalezione wyniku lub -1 gdy nie istnieje
	 */
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

	/**
	 * funkcja odwrotna do {@code pozycjaKluczaWMapie(zrodlo, klucz)}
	 * @param zrodlo mapa<K, V>
	 * @param pozycja indeks szukanego klucza
	 * @return klucz lub null jeśli nie ma takiej pozycji
	 */
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

	/**
	 * zwraca mape z parami klucza, wartosci w wysokości nie większej niż {@code pozycji}
	 * @param zrodlo mapa<Klucz, Wartosc>
	 * @param pozycji ilosc pozycji
	 * @return nowaMapa
	 */
	public static <K, V> SortedMap<K, V> goraMapy(SortedMap<K, V> zrodlo, long pozycji)
	{
		if (pozycji <= 0)
		{
			return new TreeMap<K, V>();
		}
		if (pozycji > zrodlo.size())
		{
			return zrodlo;
		}
		K doKlucza = zwrocKlucz(zrodlo, pozycji);
		return zrodlo.headMap(doKlucza);
	}
	
	@SuppressWarnings("unchecked")
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

	public static <K, V> K zwrocKlucz(SortedMap<K, V> zrodlo, V wartosc)
	{
		for (Map.Entry<K, V> wpis : zrodlo.entrySet())
		{
			if (wpis.getValue().equals(wartosc))
			{
				return wpis.getKey();
			}
		}
		return null;
	}
	
	public static <K, V> Vector<V> wartosci(SortedMap<K, V> zrodlo)
	{
		Vector<V> wynik = new Vector<V>();
		for (Map.Entry<K, V> wpis : zrodlo.entrySet())
		{
			wynik.add(wpis.getValue());
		}
		return wynik;
	}
}
