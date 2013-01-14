package qa;

import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Vector;

public class Debugowanie
{
	/**
	 *wypisuje zawartość wektora do stdout
	 *@param wektor wektor do wypisania
	 */
	public static <T> void wektor(Vector<T> wektor)
	{
		QA.wypiszZLinia("Rozmiar: "+wektor.size());
		Iterator<T> iterator = wektor.iterator();
		while (iterator.hasNext())
		{
			QA.wypiszZLinia(iterator.next().toString());
		}
	}
	
	/**
	 * wypisuje zawartość mapy do stdout
	 * @param mapa mapa do wypisania
	 */
	public static <T, K> void mapa(Map<T, K> mapa)
	{
		QA.wypiszZLinia("Rozmiar: "+mapa.size());
		for (Entry<T, K> wpis : mapa.entrySet())
		{
			QA.wypiszZLinia(wpis.getKey().toString()+" = "+wpis.getValue().toString());
		}
	}
}
