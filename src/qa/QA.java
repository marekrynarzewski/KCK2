package qa;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Scanner;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class QA
{
	private final String sciezka = "cache\\QA";
	
	public static Scanner wejscie = new Scanner(System.in);
	
	private String pytanie = "";
	private String przymiotnik = "";
	private String X = "";
	private String przyimek = "";
	private String Y = "";
	
	private String[] elementyFrazy;
	
	private Vector<String> linki = new Vector<String>();
	
	private Map<String, Integer> listaOtoczeniaSlowKluczowych = new TreeMap<String, Integer>();
	
	private int bledneLinki = 0;
	
	private String[] niedozwolneTypy = {"pdf", "doc", "ps", "pdf" };
	
	private boolean trybLokalny = false;

	private String plikLokalny;
	
	/**
	 * @param args
	 * @throws IOException 
	 * @throws FileNotFoundException 
	 */
	public static void main(String[] args) throws IOException
	{
		QA qa = new QA();
		qa.uruchomAlgorytm();
	}
	
	public static <K, V> long pozycjaKluczaWMapie(Map<K, V> zrodlo, K klucz)
	{
		long wynik = -1;
		if (!zrodlo.containsKey(klucz))
		{
			return wynik;
		}
		else
		{
			++wynik;
			for (Map.Entry<K, V> wpis : zrodlo.entrySet())
			{
				if (!wpis.getKey().equals(klucz))
				{
					++wynik;
				}
				else
				{
					++wynik;
					break;
				}
			}
			return wynik;
		}
	}
	
	public static <K, V> K zwrocKlucz(Map<K, V> zrodlo, long pozycja)
	{
		long wskaznik = 0;
		for (Map.Entry<K, V> wpis : zrodlo.entrySet())
		{
			if (wskaznik == pozycja)
			{
				return wpis.getKey();
			}
			else
			{
				++wskaznik;
			}
		}
		return null;
	}
	
	public static <K, V> SortedMap<K, V> goraMapy(TreeMap<K, V> zrodlo, long pozycji)
	{
		K toKey = QA.zwrocKlucz(zrodlo, pozycji);
		if (toKey == null)
		{
			return new TreeMap<K, V>();
		}
		else
		{
			return zrodlo.headMap(toKey);
		}
	}
	public static void wypisz(Object str)
	{
		System.out.print(str);
	}
	
	public static void wypiszZLinia(Object str)
	{
		System.out.println(str);
	}
	
	public static void wyjscie(String wiadomosc, int numerBledu)
	{
		QA.wypiszZLinia("[Błąd] " + wiadomosc);
		System.exit(numerBledu);
	}
	
	public static void ostrzez(String wiadomosc)
	{
		QA.wypiszZLinia("[Ostrzeżenie] " + wiadomosc);
	}
	
	public static void info(String wiadomosc)
	{
		QA.wypiszZLinia("[Informacja] " + wiadomosc);
	}
	
	public void uruchomAlgorytm()
	{
		this.wprowadzPytanie();
		if (this.trybLokalny)
		{
			this.zaladujPlik();
		}
		else
		{
			this.rzucWGoogle();
			this.przetwarzajWszystkieLinki();
		}
		
		this.sortujMape();
	}
	
	private void wprowadzPytanie()
	{
		QA.wypiszZLinia("Pytanie: ");
		try
		{
			this.pytanie = QA.wejscie.nextLine();
			this.przetworzPytanie();
		}
		catch(NoSuchElementException e)
		{
			QA.wyjscie("Brak pytania", Error.brakPytania);
		}
	}
	
	private void przetworzPytanie()
	{
		QA.info("Przetwarzam pytanie");
		// TODO: polskie znaki w pytaniu
		// this.pytanie = PolskieZnaki.zastap(this.pytanie);
		this.pytanie = this.pytanie.toLowerCase();
		Pattern wzorzec = Pattern.compile("([nN]aj[a-zA-Z]+[yea]) ([a-zA-Z\\ ]+) (we|w|z|na) ([a-zA-Z\\ ]+)");
		Matcher sekwencja = wzorzec.matcher(this.pytanie);
		if (sekwencja.find())
		{
			zapiszDoPolWynikiPrzetworzenia(sekwencja);
			zalogujOPozytywnymPrzetworzeniu();
			QA.info("Pozytywny wynik przetwarzania.");
			this.przygotujElementyFrazy();
		}
		else
		{
			Logowanie.log("Nie udało mi się przetworzyć pytania '" + this.pytanie + "'.");
			QA.wyjscie("Negatywny wynik przetwarzania.", Error.bladPrzetwarzania);
		}
	}

	private void zapiszDoPolWynikiPrzetworzenia(Matcher sekwencja)
	{
		this.przymiotnik = sekwencja.group(1);
		this.X = sekwencja.group(2);
		this.przyimek = sekwencja.group(3);
		this.Y = sekwencja.group(4);
	}

	private void zalogujOPozytywnymPrzetworzeniu()
	{
		Logowanie.log("Pozytywne przetworzyłem pytania.");
		Logowanie.log("przymiotnik = '" + this.przymiotnik + "'.");
		Logowanie.log("X = '" + this.X + "'.");
		Logowanie.log("przyimek = '" + this.przyimek + "'.");
		Logowanie.log("Y = '" + this.Y + "'.");
		Logowanie.log("");
	}
	
	private void rzucWGoogle()
	{
		String fraza = this.przymiotnik + " " + this.X + " " + this.przyimek + " " + this.Y;
		QA.wypiszZLinia("Przygotowuję do wysłania frazę: " + fraza);
		this.przygotujElementyFrazy();
		Vector<String> kandydaci = new Vector<String>();
		QA.info("elementy frazy nie są puste");
		fraza = fraza.toLowerCase();
		for (int i = 1; i <= 5; ++i)
		{
			String dokument = Google.zapytajStrone(fraza, i);
			QA.info("Uzyskałem dokument na "+i+" stronie.");
			String div = Google.zwrocDivaZWynikami(dokument);
			QA.info("Uzyskuję div z wynikami.");
			Vector<String> znalezioneLinki =  Google.znajdzLinki(div);
			QA.info("Znajduję linki w znalezionym divie.");
			if (znalezioneLinki.size() == 0)
			{
				break;
			}
			kandydaci.addAll(znalezioneLinki);
			QA.info("Na stronie "+i+" znalazłem "+znalezioneLinki.size()+" linków");
		}
		this.dodajUnikalneLinki(kandydaci);
	}
	
	private void zaladujPlik()
	{
		QA.wypisz("Podaj nazwę pliku: ");
		String nazwa = QA.wejscie.nextLine();
		boolean ok = true;
		try
		{
			this.plikLokalny = Plik.zaladujPlik(nazwa);
			QA.wypiszZLinia(plikLokalny);
		}
		catch (FileNotFoundException e)
		{
			e.printStackTrace();
			ok = false;
		}
		catch (IOException e)
		{
			e.printStackTrace();
			ok = false;
		}
		if (!ok)
			QA.wyjscie("Brak pliku!", Error.brakPliku);
		Vector<String> wynik = this.pobierzOtoczenieDlaWszystkichElementówFrazy(plikLokalny);
		this.dodajDoListyOtoczeniaSlowKluczowych(wynik);
		this.sortujMape();
		Debugowanie.mapa(this.listaOtoczeniaSlowKluczowych);
	}

	private void przygotujElementyFrazy()
	{
		String[] elementyFrazy = { this.przymiotnik, this.X, this.przyimek, this.Y };
		this.elementyFrazy = elementyFrazy;
	}
	
	private void dodajUnikalneLinki(Vector<String> linki)
	{
		for (String link : linki)
		{
			if (!this.linki.contains(link))
			{
				this.linki.add(link);
			}
		}
	}
	
	private void przetwarzajLink(String link)
	{
		if (this.czytajZCache("bledne-linki").contains(link))
		{
			return;
		}
		try
		{
			if (this.dozwolonyPlik(link))
			{
				String dokument = Plik.pobierzZUrla(link);
				Logowanie.log("Pobrałem dokument dla linku '"+link+"'");
				dokument = Google.konwertujHTMLDoTekstu(dokument);
				Vector<String> otoczenie = this.pobierzOtoczenieDlaWszystkichElementówFrazy(link, dokument);
				this.dodajDoListyOtoczeniaSlowKluczowych(otoczenie);
			}
		}
		catch(IOException e)
		{
			++this.bledneLinki;
			this.zapiszDoCache("bledne-linki.cache", link);
		}
		//}
	}
	
	private Vector<String> pobierzOtoczenieDlaWszystkichElementówFrazy(String dokument)
	{
		Vector<String> rezultat = new Vector<String>();
		for (String elementFrazy : this.elementyFrazy)
		{
			rezultat.addAll(Otoczenie.przezSpacje(dokument, elementFrazy, 30));
		}
		return rezultat;
	}
	
	private Vector<String> pobierzOtoczenieDlaWszystkichElementówFrazy(String link, String dokument)
	{
		Vector<String> rezultat = new Vector<String>();
		for (String elementFrazy : this.elementyFrazy)
		{
			rezultat.addAll(Otoczenie.przezSpacje(link, dokument, elementFrazy, 30));
		}
		return rezultat;
	}
	
	private void dodajDoListyOtoczeniaSlowKluczowych(Vector<String> otoczenie)
	{
		for (String element : otoczenie)
		{
			if (!jestLiczba(element))
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
	
	private boolean jestLiczba(String element)
	{
		try
		{
			Double.parseDouble(element);
		}
		catch(NumberFormatException e)
		{
			return false;
		}
		return true;
	}

	private void zapiszDoCache(String nazwaPliku, String zawartosc)
	{
		try
		{
			Plik.dodajDoPliku(sciezka+"\\"+nazwaPliku, zawartosc+"\n");
		}
		catch(IOException e)
		{
			Plik.zapiszDoPliku(sciezka+"\\"+nazwaPliku, zawartosc+"\n");
		}
	}
	
	private void przetwarzajWszystkieLinki()
	{
		int obecnyLink = 0;
		int wszystkichLinkow = this.linki.size();
		for (String link : this.linki)
		{
			this.przetwarzajLink(link);
			QA.wypiszZLinia("Przetworzyłem "+(obecnyLink*100)/(wszystkichLinkow)+"% linków.");
			obecnyLink ++;
		}
		QA.wypiszZLinia("Przetworzyłem 100% linków.");
		if (this.bledneLinki > 0)
		{
			QA.ostrzez("Błędnych linków "+this.bledneLinki);
		}
	}
	
	private Vector<String> czytajZCache(String kontener)
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
	
	private boolean czyPolskaLitera(char znak)
	{
		return (65 <= znak && znak <=  90 && 97 <= znak && znak <= 122);
	}
	
	
	private boolean czyZawieraPolskieLitery(String slowo)
	{
		for (int i = 0; i < slowo.length(); ++ i)
		{
			if (this.czyPolskaLitera(slowo.charAt(i)))
			{
				return true;
			}
		}
		return false;
	}
	
	private void sortujMape()
	{
		ValueComparator bvc =  new ValueComparator(this.listaOtoczeniaSlowKluczowych);
		TreeMap<String, Integer> sortedMap = new TreeMap<String, Integer>(bvc);
		sortedMap.putAll(listaOtoczeniaSlowKluczowych);
		Plik.mapaDoPliku("debug\\posortowanaMapa.ini", sortedMap);
		this.listaOtoczeniaSlowKluczowych = sortedMap;
		this.goraMapy();
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
	
	private void goraMapy()
	{
		int index = 0;
		Set<Map.Entry<String, Integer>> zbiorWpisow = this.listaOtoczeniaSlowKluczowych.entrySet();
		QA.wypiszZLinia(zbiorWpisow.size());
		this.listaOtoczeniaSlowKluczowych.clear();
		java.util.Iterator<Map.Entry<String, Integer>> it = zbiorWpisow.iterator();
		while (index < 1000 && it.hasNext())
		{
			QA.wypiszZLinia("Iteracja "+index+".");
			this.listaOtoczeniaSlowKluczowych.put(it.next().getKey(), it.next().getValue());
			++index;
		}
		Plik.mapaDoPliku("debug\\mapa2.ini", this.listaOtoczeniaSlowKluczowych);
	}
	
	
}
