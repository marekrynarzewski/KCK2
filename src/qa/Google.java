package qa;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URLEncoder;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Google
{
	private static final String SzukajOnet = "http://szukaj.onet.pl/wyniki.html?qt=";
	private static final String SzukajOnetStrona =  "http://szukaj.onet.pl/0,{page},query.html?qt=";
	private static final String GoogleAPI = "http://ajax.googleapis.com/ajax/services/search/web?v=1.0&q=";
	private static final String KodowanieUTF8 = "UTF-8";
	//private static final String sciezka = "cache\\Google";
	private static final String GoogleAPI2 = "https://www.googleapis.com/customsearch/v1?";
	
		private static String[] linkiZOnetu = {
			"http://boksy.onet.pl/",
			"http://www.onet.pl",
			"http://m.onet.pl",
			"http://biznes.onet.pl",
			"http://wiadomosci.onet.pl",
			"http://poczta.onet.pl",
			"http://www.onet.pl/wszystkie/",
			"http://www.onet.pl/prywatnosc/",
			"http://natemat.onet.pl/",
			"http://secure.onet.pl/logowanie.html?app_id=65&amp;url=zarzadzaniesg.html"
	};
	
	public static String zapytajGoogla(String pytanie)
	{
		String typPlikow = "filetype:html OR filetype:doc ";
		pytanie = typPlikow+pytanie;
		//Google.zapiszFraze(pytanie);
		try
		{
			String zakodowane = URLEncoder.encode(pytanie, KodowanieUTF8);
			String adres = SzukajOnet+zakodowane;
			String dokument  = Plik.pobierzZUrla(adres);
			//Google.zapiszDoCache(zakodowane, dokument);
			return dokument;
		}
		catch (IOException e)
		{
		}
		return "";
	}
	
	public static String zapytajStrone(String pytanie, int page)
	{
		String adres = Google.SzukajOnetStrona;
		adres = adres.replaceFirst("\\{page\\}", String.valueOf(page));
		String typPlikow = "filetype:html OR filetype:doc ";
		pytanie = typPlikow+pytanie;
		try
		{
			String zakodowane = URLEncoder.encode(pytanie, Google.KodowanieUTF8);
			adres = adres+zakodowane;
			String dokument = Plik.pobierzZUrla(adres);
			return dokument;
		}
		catch (MalformedURLException e)
		{
			//e.printStackTrace();
		}
		catch (IOException e)
		{
			//e.printStackTrace();
		}		
		return "";
	}
	
	public static String usunTagiProsto(String wejscie)
	{
		boolean wTagu = false;
		String wyjscie = "";
		for (int i=0; i < wejscie.length(); ++i)
		{
		    if (!wTagu && wejscie.charAt(i) == '<')
	        {
	            wTagu = true;
	            continue;
	        }
	        if (wTagu && wejscie.charAt(i) == '>')
	        {
	            wTagu = false;
	            continue;
	        }
	        if (!wTagu)
	        {
	            wyjscie = wyjscie + wejscie.charAt(i);
	        }
		}   
		return wyjscie;
	}
	
	public static String usunWszystkieTagi(String wejscie)
	{
		String[] wyrazeniaRegularne = {"(?siu)<head[^>]*?>.*?</head>",
		"(?siu)<style[^>]*?>.*?</style>",
		"(?siu)<script[^>]*?.*?</script>",
		"(?siu)<object[^>]*?.*?</object>",
		"(?siu)<embed[^>]*?.*?</embed>",
		"(?siu)<applet[^>]*?.*?</applet>",
		"(?siu)<noframes[^>]*?.*?</noframes>",
		"(?siu)<noscript[^>]*?.*?</noscript>",
		"(?siu)<noembed[^>]*?.*?</noembed>",
		// Add line breaks before and after blocks
		"(?iu)</?((address)|(blockquote)|(center)|(del))",
		"(?iu)</?((div)|(h[1-9])|(ins)|(isindex)|(p)|(pre))",
		"(?iu)</?((dir)|(dl)|(dt)|(dd)|(li)|(menu)|(ol)|(ul))",
		"(?iu)</?((table)|(th)|(td)|(caption))",
		"(?iu)</?((form)|(button)|(fieldset)|(legend)|(input))",
		"(?iu)</?((label)|(select)|(optgroup)|(option)|(textarea))",
		"(?iu)</?((frameset)|(frame)|(iframe))",
		"(?iu)</?((span)|(br))"};
		int idx = 0;
		for (String regex : wyrazeniaRegularne)
		{
			if (idx > 8)
			{
				wejscie = wejscie.replaceAll(regex, "$0");
			}
			else
			{
				wejscie = wejscie.replaceAll(regex, " ");
			}
			idx ++;	
		}
		return Google.usunTagiProsto(wejscie);
	}
	
	
	public static String konwertujHTMLDoTekstu(String wejscie)
	{
		wejscie = wejscie.replace("><", "> <");
		wejscie = wejscie.replace("&nbsp;", " ");
		wejscie = Google.usunWszystkieTagi(wejscie);
		wejscie = wejscie.replaceAll("\\p{Punct}", " ");
		wejscie = wejscie.replaceAll("\\s\\s+", " ");
		return wejscie;
	}
	
	public static String konwertujHTMLDoTekstu(String wejscie, XLong czasDzialania)
	{
		long start = System.currentTimeMillis();
		wejscie = konwertujHTMLDoTekstu(wejscie);
		czasDzialania.set(new Long(System.currentTimeMillis()-start));
		return wejscie;
	}
	
	public static String zwrocDivaZWynikami(String dokument)
	{
		Pattern wzorzecLinka = Pattern.compile("<div class=\"boxResult2\">(.*)</div>");
		Matcher wynik = wzorzecLinka.matcher(dokument);
		if (wynik.find())
		{
			return wynik.group(1);
		}
		return "";
	}
	
	public static Vector<String> znajdzLinki(String dokument)
	{
		Vector<String> linki = new Vector<String>();
		Pattern wzorzecLinka = Pattern.compile("<a href=\"(http://\\S+)\">");
		Matcher wynik = wzorzecLinka.matcher(dokument);
		while (wynik.find())
		{
			String link = wynik.group(1);
			if (!Tablice.zawiera(Google.linkiZOnetu, link))
			{
				linki.add(link);
			}
		}
		Plik.wektorDoPliku("debug\\linki.txt", linki);
		return linki;
	}
		
	public static Vector<String> znajdzLinki(String fraza, int strona)
	{
		String dokument = zapytajStrone(fraza, strona);
		return znajdzLinki(dokument);
	}
	
	/**
	 * wysyła zapytanie do Google'a
	 * @see zapytajStrone
	 * @param pytanie - fraza
	 * @param ileStron - ilość stron
	 * @return  wektor wyników (kodów stron html)
	 */
	public static Vector<String> zapytajNStron(String pytanie, int ileStron)
	{
		Vector<String> result = new Vector<String>();
		for (int i = 0; i < ileStron; ++i)
		{
			String html = Google.zapytajStrone(pytanie, i);
			result.add(html);
		}
	
		return result;
	}
	/*
	private static void zapiszFraze(String fraza)
	{
		if (!Google.zapamietaneFrazy.contains(fraza))
		{
			Google.zapamietaneFrazy.add(fraza);
			Google.zapiszDoCache("frazy", zapamietaneFrazy);
		}
	}
	
	private static Vector<String> odczytajZapisaneFrazy()
	{
		Vector<String> rezultat = new Vector<String>();
		try
		{
			rezultat = Plik.plikDoTablicy(sciezka+"\\frazy.cache");
			return rezultat;
		}
		catch (IOException e)
		{
			e.printStackTrace();
			return rezultat;
		}
	}
	
	private static void zapiszDoCache(String fraza, String zawartosc)
	{
		Plik.zapiszDoPliku(sciezka+"\\"+fraza+".cache", zawartosc);
	}
	
	private static void zapiszDoCache(String fraza, Vector<String> zawartosc)
	{
		Plik.wektorDoPliku(sciezka+"\\"+fraza+".cache", zawartosc);
	}
	*/
}
