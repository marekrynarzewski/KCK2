package qa;

import java.util.List;

import morfologik.stemming.PolishStemmer;
import morfologik.stemming.WordData;

public class Piaskownica
{
	public static void main(String[] args)
	{
	}

	public static void lematyzacja(String slowo)
	{
		PolishStemmer ps = new PolishStemmer();
		@SuppressWarnings("unchecked")
		List<WordData> list = ps.lookup(slowo);
		for (WordData wd : list)
		{
			Ekran.wypiszZLinia("word: "+wd.getWord());
			Ekran.wypiszZLinia("stem: "+wd.getStem());
			Ekran.wypiszZLinia("tag: "+wd.getTag());
		}
	}
	

}
