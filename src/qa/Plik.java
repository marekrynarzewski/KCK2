package qa;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Map;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Plik
{
	public static final String DOMYSLNE_KODOWANIE = "utf-8";

	public static String zaladujPlik(String nazwa) throws FileNotFoundException, IOException
	{
		String wynik = "";
		FileReader czytnikPliku;
		czytnikPliku = new FileReader(nazwa);
		BufferedReader buforowanyCzytnik = new BufferedReader(czytnikPliku);
		String s;
		while ((s = buforowanyCzytnik.readLine()) != null)
		{
			wynik = wynik + s + "\n";
		}
		buforowanyCzytnik.close();
		czytnikPliku.close();
		return wynik;
	}
	
	public static Vector<String> plikDoTablicy(String nazwa) throws IOException
	{
		Vector<String> rezultat = new Vector<String>();
		FileReader czytnikPliku;
		czytnikPliku = new FileReader(nazwa);
		BufferedReader buforowanyCzytnik = new BufferedReader(czytnikPliku);
		String s;
		while ((s = buforowanyCzytnik.readLine()) != null)
		{
			rezultat.add(s);
		}
		buforowanyCzytnik.close();
		czytnikPliku.close();
		return rezultat;
	}
	
	public static String pobierzZUrla(String sciezka) throws IOException
	{
		String kodowanie = Plik.ustalStroneKodowania(sciezka);
		return pobierzZKodowaniem(sciezka, kodowanie);
	}

	private static String pobierzZKodowaniem(String sciezka, String kodowanie) throws MalformedURLException, IOException
	{
		if (kodowanie == null)
		{
			kodowanie = Plik.DOMYSLNE_KODOWANIE;
		}
		URL link = new URL(sciezka);
		URLConnection polaczenie = link.openConnection();
		polaczenie.connect();
		InputStream strumienWejscia = polaczenie.getInputStream();
		InputStreamReader czytnikStrumieniaWejscia = new InputStreamReader(strumienWejscia, kodowanie);
		BufferedReader buforowanyCzytnik = new BufferedReader(czytnikStrumieniaWejscia);
		String rezultat = new String("");
		String liniaWprowadzana;
		while ((liniaWprowadzana = buforowanyCzytnik.readLine()) != null)
		{
			rezultat = rezultat.concat(liniaWprowadzana);
		}
		buforowanyCzytnik.close();
		czytnikStrumieniaWejscia.close();
		strumienWejscia.close();
		return rezultat;
	}
	
	public static String pobierzZUrla(String sciezka, XLong czasDzialania) throws IOException
	{
		long start = System.currentTimeMillis();
		String rezultat = Plik.pobierzZUrla(sciezka);
		czasDzialania.set(System.currentTimeMillis() - start);
		return rezultat;
	}
	
	public static long zapiszDoPliku(String nazwaPliku, String zawartosc)
	{
		long przedZapisem = Plik.rozmiar(nazwaPliku);
		long poZapisie = 0;
		try
		{
			FileWriter pisaczDoPliku = new FileWriter(nazwaPliku);
			pisaczDoPliku.write(zawartosc);
			pisaczDoPliku.close();
			poZapisie = Plik.rozmiar(nazwaPliku);
		}
		catch (IOException e)
		{
			return 0;
		}
		return poZapisie - przedZapisem;
	}
	
	public static long rozmiar(String nazwaPliku)
	{
		java.io.File f = new java.io.File(nazwaPliku);
		return f.length();
	}
	
	public static long dodajDoPliku(String nazwaPliku, String zawartosc) throws IOException
	{
		long przedZapisem = Plik.rozmiar(nazwaPliku);
		long poZapisie = 0;
		try
		{
			FileWriter pisaczDoPliku = new FileWriter(nazwaPliku, true);
			pisaczDoPliku.write(zawartosc);
			pisaczDoPliku.close();
			poZapisie = Plik.rozmiar(nazwaPliku);
		}
		catch (IOException e)
		{
			return 0;
		}
		return poZapisie - przedZapisem;
	}
	
	public static String uzyskajTypPliku(String plik)
	{
		URL link;
		try
		{
			link = new URL(plik);
			URLConnection polaczenie = link.openConnection();
			String typPliku = polaczenie.getHeaderField("Content-Type");
			if (typPliku != null)
			{
				return typPliku;
			}
			else
			{
				return "";
			}
		}
		catch (MalformedURLException e)
		{
			Logowanie.log("Malformed URL");
			e.printStackTrace();
			return "";
		}
		catch (IOException e)
		{
			Logowanie.log("Occured IO Exception");
			e.printStackTrace();
			return "";
		}
	}
	
	public static long rozmiarZdalnegoPliku(String url) throws UnknownFilesize
	{
		URL link;
		try
		{
			link = new URL(url);
			URLConnection polaczenie = link.openConnection();
			String rozmiar = polaczenie.getHeaderField("Content-Length");
			if (rozmiar != null)
			{
				return Long.parseLong(rozmiar);
			}
			else
			{
				throw new UnknownFilesize();
			}
		}
		catch (MalformedURLException e)
		{
			Logowanie.log("Malformed URL");
			e.printStackTrace();
		}
		catch (IOException e)
		{
			Logowanie.log("Occured IO Exception");
			e.printStackTrace();
		}
		return 0;
	}
	
	public static <T, K> long mapaDoPliku(String plik, Map<T, K> mapa)
	{
		String zawartosc = "";
		for (Map.Entry<T, K> wpis : mapa.entrySet())
		{
			zawartosc += wpis.getKey()+" = "+wpis.getValue()+"\n";
		}
		return Plik.zapiszDoPliku(plik, zawartosc);
	}
	
	public static long wektorDoPliku(String nazwaPliku, Vector<String> wektor)
	{
		String zawartosc = "";
		for (int i = 0; i < wektor.size(); ++i)
		{
			zawartosc = zawartosc.concat(wektor.elementAt(i)+'\n');
		}
		return Plik.zapiszDoPliku(nazwaPliku, zawartosc);
	}
	
	public static String ustalStroneKodowania(String link)
	{
		String[] kandydaci = new String[2];
		kandydaci[0] = Plik.uzyskajKodowanieURLa(link);
		try
		{
			String dok = Plik.pobierzZKodowaniem(link, DOMYSLNE_KODOWANIE);
			kandydaci[1] = Plik.uzyskajKodowanieWDokumencie(dok);
		}
		catch(IOException e)
		{
			kandydaci[1] = null;
		}
		
		if (kandydaci[0] != null && kandydaci[1] != null && kandydaci[0].equals(kandydaci[1]))
		{
			return kandydaci[0];
		}
		else if (kandydaci[0] != null && kandydaci[1] != null && !kandydaci[0].equals(kandydaci[1]))
		{
			return kandydaci[1];
		}
		else if (kandydaci[0] != null || kandydaci[1] != null)
		{
			return (kandydaci[0] != null)?(kandydaci[0]):(kandydaci[1]);
		}
		else
		{
			return DOMYSLNE_KODOWANIE;
		}
	}
	
	public static String uzyskajKodowanieURLa(String link)
	{
		String kodowanie = Plik.uzyskajTypPliku(link);
		if (kodowanie.contains("charset="))
		{
			return kodowanie.substring(kodowanie.indexOf("charset=")+8);
		}
		else
		{
			return null;
		}
	}
	
	public static String uzyskajKodowanieWDokumencie(String dokument)
	{
		Pattern wzorzec = Pattern.compile("<meta http-equiv=\"Content-Type\" content=\".*?; charset=(.*?)\" />");
		Matcher znajdywacz = wzorzec.matcher(dokument);
		String kodowanie = null;
		while (znajdywacz.find())
		{
			kodowanie = znajdywacz.group(1);
		}
		return kodowanie;
	}
	
	
}
