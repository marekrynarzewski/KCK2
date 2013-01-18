package qa;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.Vector;

/**
 * klasa służąca do debugowania czy wypisywania zawartości kolekcji na ekran
 * @author Marek
 */
public class Debugowanie
{
	/**
	 * wypisuje zbiór na ekran
	 * @param zbior obiekt
	 */
	public static <T> void zbior(Set<T> zbior)
	{
		Ekran.wypiszZLinia("Rozmiar zbioru: "+zbior.size());
		for (T element : zbior)
		{
			Ekran.wypiszZLinia(element);
		}
		
	}

	/**
	 *wypisuje zawartość wektora do stdout
	 *@param wektor wektor do wypisania
	 */
	public static <T> void wektor(Vector<T> wektor)
	{
		Ekran.wypiszZLinia("Rozmiar wektora: "+wektor.size());
		Iterator<T> iterator = wektor.iterator();
		while (iterator.hasNext())
		{
			Ekran.wypiszZLinia(iterator.next());
		}
	}
	
	/**
	 * wypisuje zawartość mapy do stdout
	 * @param mapa mapa do wypisania
	 */
	public static <T, K> void mapa(Map<T, K> mapa)
	{
		Ekran.wypiszZLinia("Rozmiar mapy: "+mapa.size());
		for (Entry<T, K> wpis : mapa.entrySet())
		{
			Ekran.wypiszZLinia(wpis.getKey().toString()+" = "+wpis.getValue().toString());
		}
	}
	
	public static <T> void lista(List<T> lista)
	{
		Ekran.wypiszZLinia("Rozmiar listy: "+lista.size());
		Iterator<T> iterator = lista.iterator();
		while (iterator.hasNext())
		{
			Ekran.wypiszZLinia(iterator.next().toString());
		}
	}
	
	/**
	 * wzorowana na var_dump() z php
	 * @param obiekt do wypisania
	 */
	public static <T> void var_dump(T obiekt)
	{
		pola(obiekt);
		metody(obiekt);
	}
	
	public static <T> void metody(T obiekt)
	{
		Class<? extends Object> typ = obiekt.getClass();
		Method[] metody = typ.getMethods();
		String wynik = "";
		for (Method metoda : metody)
		{
			Class<?>[] parametry = metoda.getParameterTypes();
			String parametryx = "";
			for (Class<?> parametr : parametry)
			{
				parametryx += parametr.getName()+", ";
			}
			parametryx = parametryx.substring(0, parametryx.length()-2);
			wynik += metoda.getReturnType()+" "+metoda.getName()+"("+parametryx+")\n";
		}
		Ekran.wypiszZLinia(wynik);
	}
	
	public static <T> void pola(T obiekt)
	{
		Class<? extends Object> typ = obiekt.getClass();
		Field[] fields = typ.getDeclaredFields();
		String wynik = "";
		for (int i=0; i<fields.length; i++)
		{
			Field pole = fields[i];
		    try
			{
				wynik += pole.getName() + "( " + pole.get(obiekt) + ")\n";
			}
			catch (IllegalArgumentException e)
			{
			}
			catch (IllegalAccessException e)
			{
			}
		}
		Ekran.wypiszZLinia(wynik);
	}
}
