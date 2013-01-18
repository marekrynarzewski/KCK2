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
	@SuppressWarnings("unused")
	private static final String GoogleAPI = "http://ajax.googleapis.com/ajax/services/search/web?v=1.0&q=";
	@SuppressWarnings("unused")
	private static final String GoogleAPI2 = "https://www.googleapis.com/customsearch/v1?";
	private static final String typyPlikow = "filetype:html OR filetype:txt ";
	/**
	 * lista najczęściej pojawiających linków na stronie onetu.
	 * Służą do odrzucania ich automatycznie w funkcji znajdzLinki
	 */
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
	
	/**
	 * zwraca pierwszą stronę z wynikami 
	 * @param pytanie fraza do wyszukania
	 * @return kod strony
	 */
	public static String zapytajGoogla(String pytanie)
	{
		String typPlikow = Google.typyPlikow;
		pytanie = typPlikow+pytanie;
		try
		{
			String zakodowane = URLEncoder.encode(pytanie, Kodowanie.UTF8);
			String adres = SzukajOnet+zakodowane;
			String dokument  = Plik.pobierzZUrla(adres);
			return dokument;
		}
		catch (IOException e)
		{
		}
		return "";
	}
	
	/**
	 * zwraca wynik zapytania Google (za pośrednictwem onetu)
	 * @param pytanie szukana fraza
	 * @param strona numer strony
	 * @return
	 */
	public static String zapytajStrone(String pytanie, int strona)
	{
		String adres = Google.SzukajOnetStrona;
		adres = adres.replaceFirst("\\{page\\}", String.valueOf(strona));
		String typPlikow = Google.typyPlikow;
		pytanie = typPlikow+pytanie;
		try
		{
			String zakodowane = URLEncoder.encode(pytanie, Kodowanie.UTF8);
			adres = adres+zakodowane;
			String dokument = Plik.pobierzZUrla(adres);
			return dokument;
		}
		catch (MalformedURLException e)
		{
		}
		catch (IOException e)
		{
		}		
		return "";
	}
	
	/**
	 * usuwa wszystkie tagi 
	 * @param wejscie kod strony
	 * @return tekst zawierający słowa podzielone spacjami bez interpunkcji
	 */
	public static String konwertujHTMLDoTekstu(String wejscie)
	{
		wejscie = wejscie.replace("><", "> <");
		wejscie = wejscie.replace("&nbsp;", " ");
		wejscie = Google.usunWszystkieTagi(wejscie);
		wejscie = wejscie.replaceAll("\\p{Punct}", " ");
		wejscie = wejscie.replaceAll("\\s\\s+", " ");
		return wejscie;
	}
	
	/**
	 * zwraca tę część kodu strony zawierająca div z wynikami
	 * @param dokument kod strony
	 * @return div z wynikami
	 */
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
	
	/**
	 * we fragmencie strony wyszukuje wyrażeniem regularnym linki
	 * @param dokument kod strony html
	 * @return wektor linków
	 */
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
		return linki;
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
		for (int i = 0; i <= ileStron; ++i)
		{
			String html = Google.zapytajStrone(pytanie, i);
			result.add(html);
		}
		return result;
	}

	/**
	 * pozostawia wnętrzności lub usuwa tagi html
	 * @param wejscie kod strony
	 * @return 
	 */
	private static String usunWszystkieTagi(String wejscie)
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

	/**
	 * usuwa tagi
	 * @param wejscie kod strony
	 * @return czysty tekst
	 */
	private static String usunTagiProsto(String wejscie)
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
}
