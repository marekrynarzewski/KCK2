package qa;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import morfologik.stemming.PolishStemmer;
import morfologik.stemming.WordData;

public class QA
{
	private String przymiotnik = "";
	private String X = "";
	private String przyimek = "";
	private String Y = "";
	
	private String[] elementyFrazy;
	private String[] niedozwolneTypy = {"pdf", "doc", "ps"};
	private String[] przyimki = {"z", "do", "na", "bez", "za", "pod", "u", "w", "nad", "od", "po"};
	
	private TreeSet<String> linki = new TreeSet<String>();
	private Vector<String> bledneLinki = new Vector<String>();
	private int liczbaZlychLinkow = 0;
	
	private SortedMap<String, Integer> listaOtoczeniaSlowKluczowych = new TreeMap<String, Integer>();
	private SortedMap<String, Integer> czasyPobierania = new TreeMap<String, Integer>();
	private long czasDzialania = 0;
	
	private Cache cache = new Cache();
	private final String sciezka = QA.Cache.sciezka+"\\strony";
	private String[] podejrzaneSlowa = {"print", "print_test"};
	
	public static void main(String[] args)
	{
 		QA qa = new QA();
		qa.uruchomAlgorytm();
	}
	
	/**
	 * metoda główna klasy
	 * pobiera pytanie, przetwarza, odpytuje Google'a
	 * pobiera wyniki, wyciąga prawdopodobne odpowiedzi
	 * i wypisuje na ekran
	 */
	public void uruchomAlgorytm()
	{
		this.liczCzas();
		this.inicjujCache();
		this.wprowadzPytanie();
		this.rzucWGoogle();
		this.przetwarzajWszystkieLinki();
		this.sortujMape();
		this.listaOtoczeniaSlowKluczowych = Tablice.goraMapy(this.listaOtoczeniaSlowKluczowych, 1000);
		this.odrzucPrzyimki(this.listaOtoczeniaSlowKluczowych);
		this.sugestieOdpowiedzi();
		this.czasDzialaniaAlgorytmu();
		/*
		 * this.start();
		 * this.wprowadzPytanie();
		 * this.poszukajWInternecie();
		 * this.
		 * this.koniec();
		 */
		
	}

	public String uruchomAlgorytm(String pytanie)
	{
		return "";
	}
	/**
	 * inicjuje pamięć podręczną używaną do zapisu błędnych linków (nie pobiera ich 2 raz i plików
	 * , które pierwsze pobranie trwało długo
	 */
	private void inicjujCache()
	{
		this.bledneLinki = this.cache.czytaj("bledne-linki");
		this.liczbaZlychLinkow = 0;
		for (String linia : this.cache.czytaj("czas-przetwarzania"))
		{
			String[] elementy = linia.split(" ");
			this.czasyPobierania.put(elementy[0], Integer.valueOf(elementy[1]));
		}
		//czasyPobierania = this.sortujMape(czasyPobierania);
	}

	/**
	 * prosi o wpisanie pytania, na które poszuka program odpowiedzi.
	 */
	private void wprowadzPytanie()
	{
		Ekran.wypiszZLinia("Pytanie: ");
		try
		{
			String pytanie = Ekran.wejscie.nextLine();
			this.przetworzPytanie(pytanie);
		}
		catch(NoSuchElementException e)
		{
			Ekran.wyjscie("Brak pytania", Error.brakPytania);
		}
	}
	
	/**
	 * przetwarza pytanie, 
	 * dokonuje ekstrakcja przymiotnika, 
	 * rzeczownika X, przyimka i rzeczownika Y
	 * @param pytanie pytanie do przetworzenia
	 */
	private void przetworzPytanie(String pytanie)
	{
		Ekran.info("Przetwarzam pytanie");
		pytanie = pytanie.toLowerCase();
		Pattern wzorzec = Pattern.compile("(naj[a-zA-Z]+[yea]) ([a-zA-Z\\ ]+) (we|w|z|na| )?([a-zA-Z\\ ]+)");
		Matcher sekwencja = wzorzec.matcher(pytanie);
		if (sekwencja.find())
		{
			this.przymiotnik = sekwencja.group(1);
			this.X = sekwencja.group(2);
			this.przyimek = sekwencja.group(3);
			this.Y = sekwencja.group(4);
			Logowanie.log("Pozytywne przetworzyłem pytanie.");
			Logowanie.log("przymiotnik = '" + this.przymiotnik + "'.");
			Logowanie.log("X = '" + this.X + "'.");
			Logowanie.log("przyimek = '" + this.przyimek + "'.");
			Logowanie.log("Y = '" + this.Y + "'.");
			Logowanie.log("");
			Ekran.info("Pozytywny wynik przetwarzania.");
			this.przygotujElementyFrazy();
		}
		else
		{
			Logowanie.log("Nie udało mi się przetworzyć pytania '" + pytanie + "'.");
			Ekran.wyjscie("Negatywny wynik przetwarzania.", Error.bladPrzetwarzania);
		}
	}
	
	/**
	 * odpytowuje Google'a zadaną frazą
	 */
	private void rzucWGoogle()
	{
		String fraza = this.przymiotnik + " " + this.X + " " + this.przyimek + " " + this.Y;
		Ekran.wypiszZLinia("Przygotowuję do wysłania frazę: " + fraza);
		Vector<String> kandydaci = new Vector<String>();
		fraza = fraza.toLowerCase();
		//TODO: wykorzystać funkcję siec.Google.zapytajNStron
		for (int i = 1; i <= 10; ++i)
		{
			String dokument = Google.zapytajStrone(fraza, i);
			Ekran.info("Uzyskałem dokument na "+i+" stronie.");
			String div = Google.zwrocDivaZWynikami(dokument);
			Ekran.info("Uzyskuję div z wynikami.");
			Vector<String> znalezioneLinki =  Google.znajdzLinki(div);
			Ekran.info("Znajduję linki w znalezionym divie.");
			if (znalezioneLinki.size() == 0)
			{
				Ekran.wypiszZLinia(znalezioneLinki.size());
				break;
			}
			kandydaci.addAll(znalezioneLinki);
			Ekran.info("Na stronie "+i+" znalazłem "+znalezioneLinki.size()+" linków");
		}
		this.dodajUnikalneLinki(kandydaci);
		/*
		Vector<String> strony = Google.zapytajNStron(fraza, 10);
		for (String strona : strony)
		{
			strona = Google.zwrocDivaZWynikami(strona);
			Vector<String> znalezioneLinki = Google.znajdzLinki(strona);
			kandydaci.addAll(znalezioneLinki);
			int linkow = znalezioneLinkow.size();
			if (linkow == 0)
				break;
		}
		 */ 
		 
	}
	
	/**
	 * zapisuje do tablicy elementy frazy
	 * @see pobierzOtoczenieDlaWszystkichElementówFrazy
	 */
	private void przygotujElementyFrazy()
	{
		String[] elementyFrazy = { this.przymiotnik, this.X, this.przyimek, this.Y };
		this.elementyFrazy = elementyFrazy;
	}
	
	/**
	 * dodaje do prywatnego wektora zawartość z parametru tylko unikalne wartości
	 * @param linki - wektor słów
	 */
	private void dodajUnikalneLinki(Vector<String> linki)
	{
		this.linki.addAll(linki);
	}
	
	/**
	 * pobiera otoczenia dla wszystkich elementów frazy
	 * @param dokument zrodlo
	 * @return tablice slow w otoczeniu slow kluczowych
	 */
	private Vector<String> pobierzOtoczenieDlaWszystkichElementówFrazy(String dokument)
	{
		Vector<String> rezultat = new Vector<String>();
		for (String elementFrazy : this.elementyFrazy)
		{
			rezultat.addAll(Otoczenie.przezSpacje(dokument, elementFrazy, 30));
		}
		return rezultat;
	}
	
	/**
	 * funkcja diagnostyczna 
	 * @see pobierzOtoczenieDlaWszystkichElementowFrazy
	 * @param link - skad pochodzi dokument
	 * @param dokument - zrodlo
	 * @return tablica
	 */
	private Vector<String> pobierzOtoczenieDlaWszystkichElementówFrazy(String link, String dokument)
	{
		Vector<String> rezultat = new Vector<String>();
		for (String elementFrazy : this.elementyFrazy)
		{
			rezultat.addAll(Otoczenie.przezSpacje(link, dokument, elementFrazy, 30));
		}
		return rezultat;
	}
	
	/**
	 * dodaje elementy z wektora do mapy zliczającej ile takich slow wystapilo w tekstach
	 * @param otoczenie - wektor slow otoczenia
	 */
	private void dodajDoListyOtoczeniaSlowKluczowych(Vector<String> otoczenie)
	{
		for (String element : otoczenie)
		{
			element = element.trim();
			if (!element.matches("[0-9]+.*") && !element.isEmpty())
			{
				if (!this.listaOtoczeniaSlowKluczowych.containsKey(element))
				{
					this.listaOtoczeniaSlowKluczowych.put(element, 1);
				}
				else
				{
					int wartosc = this.listaOtoczeniaSlowKluczowych.get(element);
					this.listaOtoczeniaSlowKluczowych.put(element, wartosc+1);
				}
			}
		}
	}
	
	/**
	 * klasa obsługująca pamiec podreczna dla QA
	 * @author Marek
	 *
	 */
	class Cache
	{
		/**
		 * sciezka zapisu danych pamieci podrecznej
		 */
		private final static String sciezka = "cache\\QA";
		
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
	}

	/**
	 * przetwarza link
	 * @param link - url do przetworzenia
	 */
	private void przetwarzajLink(String link)
	{
		if 
		(
				this.bledneLinki.contains(link) 
				&& czyZawieraPodejrzaneSlowa(link)
		)
		{
			return;
		}
		try
		{
			if (this.dozwolonyPlik(link))
			{
				long czasPrzetwarzania = System.currentTimeMillis();
				String dokument = Plik.pobierzZUrla(link);
				Logowanie.log("Pobrałem dokument dla linku '"+link+"'");
				dokument = Google.konwertujHTMLDoTekstu(dokument);
				Vector<String> otoczenie = this.pobierzOtoczenieDlaWszystkichElementówFrazy(link, dokument);
				this.dodajDoListyOtoczeniaSlowKluczowych(otoczenie);
				czasPrzetwarzania = System.currentTimeMillis() - czasPrzetwarzania;
				if (czasPrzetwarzania > 20000)
				{
					try
					{
						link = URLEncoder.encode(link, Kodowanie.UTF8);
						this.zapiszDoCache(link, dokument);
					}
					catch(UnsupportedEncodingException e)
					{
						/*
						long numer = this.nowyNumerPlikuPamieciPodrecznej();
						this.zapiszDoCache(numer, dokument);
						*/
					}
				}
				this.zapiszCzasPrzetwarzania(link, czasPrzetwarzania);
			}
		}
		catch(IOException e)
		{
			this.bledneLinki.add(link);
			++this.liczbaZlychLinkow;
			this.cache.zapisz("bledne-linki", link);
		}
	}
	
	private boolean czyCzasPobieraniaJestDlugi(String link)
	{
		Integer czasPobierania = this.czasyPobierania.get(link);
		if (czasPobierania < 20000)
		{
			return true;
		}
		return false;
	}
	
	private void zapiszDoCache(String nazwaPliku, String dokument)
	{
		Katalog.utworz(sciezka);
		String sciezka = QA.Cache.sciezka+"\\"+nazwaPliku;
		Plik.zapiszDoPliku(sciezka, dokument);
	}

	private void zapiszCzasPrzetwarzania(String link, long czas)
	{
		if (!this.czasyPobierania.containsKey(link))
		{
			this.cache.zapisz("czas-przetwarzania", link+" "+czas);
		}
	}

	private void przetwarzajWszystkieLinki()
	{
		int obecnyLink = 0;
		int wszystkichLinkow = this.linki.size();
		for (String link : this.linki)
		{
			this.przetwarzajLink(link);
			Ekran.wypiszZLinia("Przetworzyłem "+(obecnyLink*100)/(wszystkichLinkow)+"% linków.");
			obecnyLink ++;
		}
		Ekran.wypiszZLinia("Przetworzyłem 100% linków.");
		if (this.bledneLinki.size() > 0)
		{
			Ekran.ostrzez("Błędnych linków "+this.liczbaZlychLinkow);
		}
	}
	
	private boolean dozwolonyPlik(String plik)
	{
		for (String rozszerzenie : this.niedozwolneTypy)
		{
			if (plik.endsWith(rozszerzenie))
			{
				return false;
			}
		}
		return true;
	}
	
	private void czasDzialaniaAlgorytmu()
	{
		long czasDzialania = System.currentTimeMillis() - this.czasDzialania;
		Calendar liczCzasOd = Calendar.getInstance();
		liczCzasOd.setTimeInMillis(czasDzialania);
		SimpleDateFormat prostyFormatDaty = new SimpleDateFormat("mm:ss");
		String data = prostyFormatDaty.format(liczCzasOd.getTime());
		Ekran.wypiszZLinia("Czas działania algorytmu: "+data);
	}

	private void liczCzas()
	{
		this.czasDzialania = System.currentTimeMillis();
	}
	
	private void odrzucPrzyimki(SortedMap<String, Integer> posortowanaMapa)
	{
		SortedMap<String, Integer> odfiltrowanaMapa = new TreeMap<String, Integer>();
		for (Map.Entry<String, Integer> wpis : posortowanaMapa.entrySet())
		{
			String slowo = wpis.getKey();
			slowo = slowo.trim();
			if (!Tablice.zawiera(this.przyimki, slowo))
			{
				odfiltrowanaMapa.put(slowo, wpis.getValue());
			}
		}
		posortowanaMapa = odfiltrowanaMapa;
	}
	
	/**
	 * dla posortowanej mapy uzyskuje formy podstawowe i zapisuje w pliku
	 */
	@SuppressWarnings("unchecked")
	private void sugestieOdpowiedzi()
	{
		SortedMap<String, Integer> nowaMapa = new TreeMap<String, Integer>();
		List<WordData> wpisy = new ArrayList<WordData>();
		for (Map.Entry<String, Integer> wpis : this.listaOtoczeniaSlowKluczowych.entrySet())
		{
			String odpowiedz = wpis.getKey();
			PolishStemmer ps = new PolishStemmer();
			wpisy = ps.lookup(odpowiedz);
			if (!wpisy.isEmpty())
			{
				String formaPodstawowa = wpisy.get(0).getStem().toString();
				if (!nowaMapa.containsKey(formaPodstawowa))
				{
					nowaMapa.put(formaPodstawowa, wpis.getValue());
				}
				else
				{
					int value = nowaMapa.get(formaPodstawowa);
					value = wpis.getValue()+value;
					nowaMapa.put(formaPodstawowa, value);
				}
			}
		}
		nowaMapa = this.sortujMape(nowaMapa);
		Plik.mapaDoPliku("debug\\mapa4.ini", nowaMapa);
	}

	private void sortujMape()
	{
		ValueComparator bvc =  new ValueComparator(this.listaOtoczeniaSlowKluczowych);
		TreeMap<String, Integer> SortedMap = new TreeMap<String, Integer>(bvc);
		SortedMap.putAll(this.listaOtoczeniaSlowKluczowych);
		Plik.mapaDoPliku("debug\\posortowanaMapa.ini", SortedMap);
		this.listaOtoczeniaSlowKluczowych = SortedMap;
	}

	private SortedMap<String, Integer> sortujMape(SortedMap<String, Integer> mapa)
	{
		ValueComparator bvc =  new ValueComparator(mapa);
		SortedMap<String, Integer> map = new TreeMap<String, Integer>(bvc);
		map.putAll(mapa);
		return map;
	}
	
	private boolean czyZawieraPodejrzaneSlowa(String link)
	{
		return Tablice.zawieraja(this.podejrzaneSlowa, link);
	}
}
