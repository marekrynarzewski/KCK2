package qa;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.Vector;

public class PamiecPodreczna
{
	private SortedMap<Long, String> indeks = new TreeMap<Long, String>();
	
	/**
	* sciezka zapisu danych pamieci podrecznej
	*/
	public String sciezka = "cache\\QA\\";
		
	/**
	 * tworzy pamiec podreczna w podkatalogu głównej pamięci podrecznej
	 * @param sciezka
	 */
	public PamiecPodreczna(String sciezka)
	{
		this.sciezka += sciezka;
		this.indeks = this.uzyskajIndeksPamieciPodrecznej();
	}
	
	/**
	 * zapisuje w kontenerze (pliku o rozszerzeniu .cache wartosc
	 * @param kontener - nazwa pliku
	 * @param zawartosc - dane do zapisania
	 */
		public void zapisz(String kontener, String zawartosc)
		{
			try
			{
				Plik.dodajDoPliku(sciezka+"\\"+kontener+".cache", zawartosc+"\n");
			}
			catch(IOException e)
			{
				Plik.zapiszDoPliku(sciezka+"\\"+kontener+".cache", zawartosc+"\n");
			}
		}
		
		/**
		 * czyta dane z pamieci podrecznej
		 * @param kontener - nazwa pliku
		 * @return wektor
		 */
		public Vector<String> czytaj(String kontener)
		{
			Vector<String> wynik = new Vector<String>();
			try
			{
				return Plik.plikDoTablicy(sciezka+"\\"+kontener+".cache");
			}
			catch (IOException e)
			{
				return wynik;
			}
		}
		
		public String czytajPlik(String nazwaPliku)
		{
			try
			{
				return Plik.zaladujPlik(sciezka+"\\"+nazwaPliku);
			}
			catch (FileNotFoundException e)
			{
			}
			catch (IOException e)
			{
			}
			return "";
		}
		
	public long uzyskajIndeks(String nazwaPliku)
	{
		if (indeks.isEmpty())
		{
			return 1;
		}
		else
		{
			return indeks.lastKey()+1;
		}
	}

	private SortedMap<Long, String> uzyskajIndeksPamieciPodrecznej()
	{
		SortedMap<Long, String> wynik = new TreeMap<Long, String>();
		//Vector<String> zawartoscIndeksu = Plik.plikDoTablicy(sciezka+"\\strony\\index.index");
		Vector<String> zawartoscIndeksu = this.czytaj("index");
		for (String linijka : zawartoscIndeksu)
		{
			String[] elementy = linijka.split(" ");
			wynik.put(Long.valueOf(elementy[0]), elementy[1]);
		}
		return wynik;
	}
	
}
