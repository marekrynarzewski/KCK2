package qa;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeSet;
import java.util.Vector;

public class Debugowanie
{
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
		Ekran.wypiszZLinia("Rozmiar: "+wektor.size());
		Iterator<T> iterator = wektor.iterator();
		while (iterator.hasNext())
		{
			Ekran.wypiszZLinia(iterator.next().toString());
		}
	}
	
	/**
	 * wypisuje zawartość mapy do stdout
	 * @param mapa mapa do wypisania
	 */
	public static <T, K> void mapa(Map<T, K> mapa)
	{
		Ekran.wypiszZLinia("Rozmiar: "+mapa.size());
		for (Entry<T, K> wpis : mapa.entrySet())
		{
			Ekran.wypiszZLinia(wpis.getKey().toString()+" = "+wpis.getValue().toString());
		}
	}
	
	public static <T> void lista(List<T> lista)
	{
		Ekran.wypiszZLinia("Rozmiar: "+lista.size());
		Iterator<T> iterator = lista.iterator();
		while (iterator.hasNext())
		{
			Ekran.wypiszZLinia(iterator.next().toString());
		}
	}
	
	public static <T> void var_dump(T obiekt)
	{
		Class<?> type = obiekt.getClass();
		String wynik = type.getName()+"\n{\n";
		
		Field[] fields = type.getDeclaredFields();
		for (int i=0; i<fields.length; i++)
		{
			Field pole = fields[i];
		    try
			{
				wynik += pole.getName() + "( " + pole.get(obiekt) + ")\n";
			}
			catch (IllegalArgumentException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			catch (IllegalAccessException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		Method[] metody = type.getMethods();
		for (Method metoda : metody)
		{
			Class<?>[] parametry = metoda.getParameterTypes();
			String parametryx = "";
			for (Class<?> parametr : parametry)
			{
				parametryx += parametr.getName()+", ";
			}
			wynik += metoda.getReturnType()+" "+metoda.getName()+"("+parametryx+")\n";
		}
		wynik += "}";
		Ekran.wypiszZLinia(wynik);
	}
}
