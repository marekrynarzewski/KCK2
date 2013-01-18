package qa;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;
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
	private String[] przyimki = {"z", "do", "na", "bez", "za", "pod", "u", "w", "nad", "od", "po", "we"};
	
	private Set<String> linki = new TreeSet<String>();
	private Vector<String> bledneLinki = new Vector<String>();
	private Vector<String> zapisanePliki = new Vector<String>();
	private int liczbaZlychLinkow = 0;
	
	private SortedMap<String, Integer> listaOtoczeniaSlowKluczowych = new TreeMap<String, Integer>();
	private SortedMap<String, Integer> czasyPobierania = new TreeMap<String, Integer>();
	private long czasDzialania = 0;
	private long maksymalnyCzasPrzetwarzania = 15000;
	
	private PamiecPodreczna pamiecPodreczna = new PamiecPodreczna("");
	private final String sciezka = "cache\\QA\\strony";
	private PamiecPodreczna pamiecPodrecznaDlaPlikow = new PamiecPodreczna("strony");
	private String[] podejrzaneSlowa = {"print", "print_test"};
	
	private int ileStronPytac = 10;
	
	public static void main(String[] args)
	{
		if (args.length > 0)
		{
			Piaskownica.main(args);
		}
		else
		{
			QA qa = new QA();
			qa.uruchomAlgorytm();
		}
	}
	
	/**
	 * metoda główna klasy
	 * pobiera pytanie, przetwarza, odpytuje Google'a
	 * pobiera wyniki, wyciąga prawdopodobne odpowiedzi
	 * i wypisuje na ekran
	 */
	public void uruchomAlgorytm()
	{
		this.inicjuj();
		this.wprowadzPytanie();
		this.rzucWGoogle();
		this.przetwarzajWszystkieLinki();
		this.listaOtoczeniaSlowKluczowych = Tablice.goraMapy(this.listaOtoczeniaSlowKluczowych, 1000);
		this.odrzucPrzyimki(this.listaOtoczeniaSlowKluczowych);
		this.sugestieOdpowiedzi();
		this.czasDzialaniaAlgorytmu();
		/*
		 * this.zapytajGoogla();
		 * this.znajdzLinkiWWynikachWyszukiwania();
		 * this.odwiedzLinki();
		 * this.goraMapy();
		 * this.odrzucPrzyimkiISlowaKluczowe();
		 * this.zastosujMorfologik();
		 * this.?();
		 * this.koniecAlgorytmu();
		 */
		
	}

	public String uruchomAlgorytm(String pytanie)
	{
		this.przetworzPytanie(pytanie);
		this.rzucWGoogle();
		this.przetwarzajWszystkieLinki();
		/*
		 * this.goraMapy();
		 * this.odrzucPrzyimkiISlowaKluczowe();
		 * this.zastosujMorfologik();
		 * this.?();
		 * return this.sugestieOdpowiedzi();
		 */
		return "";
	}
	
	/**
	 * inicjuje pamiec podreczna,
	 * wczytuje zapisane w pamieci pliki
	 * i uruchamia czas
	 */
	private void inicjuj()
	{
		this.inicjujPamiec();
		this.inicjujZapamietanePliki();
		this.liczCzas();
	}

	/**
	 * inicjuje pamięć podręczną 
	 */
	private void inicjujPamiec()
	{
		this.bledneLinki = this.pamiecPodreczna.czytaj("bledne-linki");
		this.liczbaZlychLinkow = 0;
		for (String linia : this.pamiecPodreczna.czytaj("czas-przetwarzania"))
		{
			String[] elementy = linia.split(" ");
			this.czasyPobierania.put(elementy[0], Integer.valueOf(elementy[1]));
		}
	}

	private void inicjujZapamietanePliki()
	{
		//SortedMap<Long, String> indeks = this.uzyskajIndeksPamieciPodrecznej();
		//this.zapisanePliki = Tablice.wartosci(indeks);
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
		Pattern wzorzec = Pattern.compile("(naj[a-zA-Z]+[yea]) ([a-zA-Z\\ ]+) (we|w|z|na) ([a-zA-Z\\ ]+)");
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
		Vector<String> strony = Google.zapytajNStron(fraza, this.ileStronPytac);
		Ekran.info("Uzyskałem wyniki zapytania na "+strony.size()+" stronach.");
		int i = 0;
		for (String strona : strony)
		{
			strona = Google.zwrocDivaZWynikami(strona);
			Vector<String> znalezioneLinki = Google.znajdzLinki(strona);
			kandydaci.addAll(znalezioneLinki);
			int linkow = znalezioneLinki.size();
			Ekran.info("Na stronie "+i+" znalazłem "+linkow+" linków");
			if (linkow == 0)
				break;
			++i;
		}
		this.linki.addAll(kandydaci);
		Ekran.wypiszZLinia("Wszystkich linków: "+this.linki.size());
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
	

	/**
	 * przetwarza link
	 * @param link - url do przetworzenia
	 */
	private void przetwarzajLink(String link)
	{
		if (this.sprawdzCzyBlednyICzyNieZawieraPodejrzanychSlow(link))
			return;
		try
		{
			if (this.sprawdzCzyZapisanyDoPamieciPodrecznejIPrzywroc(link))
				return;
			if (this.dozwolonyPlik(link))
			{
				long czasPrzetwarzania = System.currentTimeMillis();
				String dokument = Plik.pobierzZUrla(link);
				Logowanie.log("Pobrałem dokument dla linku '"+link+"'");
				dokument = Google.konwertujHTMLDoTekstu(dokument);
				Vector<String> otoczenie = this.pobierzOtoczenieDlaWszystkichElementówFrazy(dokument);
				this.dodajDoListyOtoczeniaSlowKluczowych(otoczenie);
				czasPrzetwarzania = System.currentTimeMillis() - czasPrzetwarzania;
				if (czasPrzetwarzania > this.maksymalnyCzasPrzetwarzania)
				{
					this.zapisanePliki.add(link);
					long indeksPliku = this.nowyIndeksPliku();
					this.zapiszDoCache(indeksPliku, dokument);
				}
				this.zapiszCzasPrzetwarzania(link, czasPrzetwarzania);
			}
		}
		catch(IOException e)
		{
			this.bledneLinki.add(link);
			++this.liczbaZlychLinkow;
			this.pamiecPodreczna.zapisz("bledne-linki", link);
		}
	}

	private boolean sprawdzCzyZapisanyDoPamieciPodrecznejIPrzywroc(String link) throws UnsupportedEncodingException, FileNotFoundException, IOException
	{
		if (this.zapisanePliki.contains(link))
		{
			long indeksPliku = this.pamiecPodrecznaDlaPlikow.uzyskajIndeks(link);
			String dokument = Plik.zaladujPlik(sciezka+"\\strony\\"+indeksPliku);
			dokument = this.pamiecPodrecznaDlaPlikow.czytajPlik(indeksPliku+"");
			Vector<String> otoczenie = this.pobierzOtoczenieDlaWszystkichElementówFrazy(dokument);
			this.dodajDoListyOtoczeniaSlowKluczowych(otoczenie);
			return true;
		}
		return false;
	}

	private boolean sprawdzCzyBlednyICzyNieZawieraPodejrzanychSlow(String link)
	{
		if 
		(
				this.bledneLinki.contains(link) 
				&& !czyZawieraPodejrzaneSlowa(link)
		)
		{
			return true;
		}
		else
		{
			return false;
		}
	}
	
	@SuppressWarnings("unused")
	private boolean czyCzasPobieraniaJestDlugi(String link)
	{
		Integer czasPobierania = this.czasyPobierania.get(link);
		if (czasPobierania < this.maksymalnyCzasPrzetwarzania)
		{
			return true;
		}
		return false;
	}
	
	private void zapiszDoCache(long indeksPliku, String dokument)
	{
		this.pamiecPodrecznaDlaPlikow.zapisz(""+indeksPliku, dokument);
	}

	private void zapiszCzasPrzetwarzania(String link, long czas)
	{
		if (!this.czasyPobierania.containsKey(link))
		{
			this.pamiecPodreczna.zapisz("czas-przetwarzania", link+" "+czas);
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
		PolishStemmer ps = new PolishStemmer();
		for (Map.Entry<String, Integer> wpis : this.listaOtoczeniaSlowKluczowych.entrySet())
		{
			String odpowiedz = wpis.getKey();
			
			wpisy = ps.lookup(odpowiedz);
			if (!wpisy.isEmpty())
			{
				WordData wd = wpisy.get(0);
				String formaPodstawowa = wd.getStem().toString();
				String tagi = wd.getTag().toString();
				String[] tag = tagi.split(":");
				if (tag.length >= 1 && tag[0] != null && tag[0].equals("subst"))
				{
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
		}
		nowaMapa = this.sortujMape(nowaMapa);
		Plik.mapaDoPliku("debug\\mapa4.ini", nowaMapa);
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
		return Tablice.zawieraja(this.getPodejrzaneSlowa(), link);
	}
	
	/**
	 * zwraca pierwszy wolny indeks pamieci podrecznej QA
	 * @return numer indeksu
	 */
	private long nowyIndeksPliku()
	{
		//SortedMap<Long, String> indeks = this.uzyskajIndeksPamieciPodrecznej();
		//if (indeks.isEmpty())
		{
			return 1;
		}
		//else
		//{
			//return indeks.lastKey()+1;
		//}
	}
	
	

	public String[] getPodejrzaneSlowa()
	{
		return podejrzaneSlowa;
	}

	public void setPodejrzaneSlowa(String[] podejrzaneSlowa)
	{
		this.podejrzaneSlowa = podejrzaneSlowa;
	}
	
	private void goraMapy()
	{
		this.listaOtoczeniaSlowKluczowych = Tablice.goraMapy(this.listaOtoczeniaSlowKluczowych, 1000);
	}
	
	private void odrzucPrzyimkiISlowaKluczowe()
	{
		SortedMap<String, Integer> nowaMapa = new TreeMap<String, Integer>();
		for (Map.Entry<String, Integer> wpis : this.listaOtoczeniaSlowKluczowych.entrySet())
		{
			String slowo = wpis.getKey();
			slowo = slowo.trim();
			if 
			(
				!Tablice.zawiera(this.przyimki, slowo)
				&& 
				!Tablice.zawiera(this.elementyFrazy, slowo)
			)
			{
				nowaMapa.put(slowo, wpis.getValue());
			}
		}
		this.listaOtoczeniaSlowKluczowych = nowaMapa;
	}
	
	private void zastosujMorfologik()
	{
		this.sugestieOdpowiedzi();
	}
}
