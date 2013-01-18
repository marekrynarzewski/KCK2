package qa;

import java.util.List;
import java.util.Vector;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Otoczenie
{
	public static Vector<String> przezSpacje(String wejscie, String slowo, int ileWyrazow)
	{
		Vector<String> rezultat = new Vector<String>();
		String[] slowa = wejscie.split(" ");
		Vector<Integer> wszystkiePozycje = Tablice.znajdzWszystkie(slowa, slowo);
		Vector<String> wektor = Tablice.doWektora(slowa);
		for (Integer pozycja : wszystkiePozycje)
		{
			int indeksPoczatkowy = pozycja - ileWyrazow;
			if (indeksPoczatkowy < 0)
			{
				indeksPoczatkowy = 0;
			}
			int indeksKoncowyWylaczony = pozycja + ileWyrazow;
			if (indeksKoncowyWylaczony > wektor.size())
			{
				indeksKoncowyWylaczony = wektor.size();
			}
			List<String> podRezultat = wektor.subList(indeksPoczatkowy, indeksKoncowyWylaczony);
			rezultat.addAll(podRezultat);
		}
		return rezultat;
	}
	
	public static Vector<String> wyrazeniamiRegularnymi(String wejscie, String slowo, int ileWyrazow)
	{
		Vector<String> rezultat = new Vector<String>();
		Pattern wzorzec = Pattern.compile("([\\węóąśłżźćńĘÓĄŚŁŻŹĆŃ]+\\s*){0," + ileWyrazow + "}\\s+" + slowo + "\\s+([\\węóąśłżźćńĘÓĄŚŁŻŹĆŃ]+\\s*){0," + ileWyrazow + "}");
		Matcher wyniki = wzorzec.matcher(wejscie);
		while (wyniki.find())
		{
			for (int i = 1; i <= wyniki.groupCount(); ++i)
			{
				String[] slowa = wyniki.group(i).split(" ");
				for (String word:slowa)
				{
					rezultat.add(word);
				}
			}
		}
		return rezultat;
	}
}
