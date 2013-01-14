package qa;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Logowanie
{
	private static final String sciezkaLogowania = "logs";
	private static String plikLogowania;
	
	private static boolean pierwszeLogowanie = true;

	public static void log(String wiadomosc)
	{
		if (pierwszeLogowanie)
		{
			nowyKatalogLogowania();
			nowyPlikLogowania();
			pierwszeLogowanie = false;

		}
		Logowanie.zapiszDoPlikuLogowania(uzyskajDateICzasWMoimFormacie()+" "+wiadomosc+"\n");
	}
	
	private static String uzyskajDateWMoimFormacie()
	{
		Date d = new Date();
		SimpleDateFormat f = new SimpleDateFormat("yyyyMMdd");
		return f.format(d);
	}
	
	private static String uzyskajCzasWMoimFormacie()
	{
		Date d = new Date();
		SimpleDateFormat f = new SimpleDateFormat("HHmmss");
		return f.format(d);
	}
	
	private static String uzyskajDateICzasWMoimFormacie()
	{
		return uzyskajDateWMoimFormacie()+" "+uzyskajCzasWMoimFormacie();
	}
	
	private static void zapiszDoPlikuLogowania(String wiadomosc)
	{
		try
		{
			Plik.dodajDoPliku(plikLogowania, wiadomosc);
		}
		catch (IOException e)
		{
			Plik.zapiszDoPliku(plikLogowania, wiadomosc);
		}
	}
	
	private static void nowyPlikLogowania()
	{
		String nowyPlikLogowania = plikLogowania+"\\"+uzyskajCzasWMoimFormacie()+".log";
		Plik.zapiszDoPliku(nowyPlikLogowania, "");
		plikLogowania = nowyPlikLogowania;
	}
	
	private static void nowyKatalogLogowania()
	{
		String nowaSciezkaLogowania = sciezkaLogowania+"\\"+uzyskajDateWMoimFormacie();
		java.io.File f = new java.io.File(nowaSciezkaLogowania);
		f.mkdir();
		plikLogowania = nowaSciezkaLogowania;
	}
}
