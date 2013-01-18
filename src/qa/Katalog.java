package qa;

import java.io.File;
import java.util.Vector;

class Katalog
{
	public static boolean utworz(String nazwaKatalogu)
	{
		File f = new File(nazwaKatalogu);
		return f.mkdir();
	}
	
	public static Vector<String> skanuj(String nazwaKatalogu)
	{
		Vector<String> plikiWKatalogu = new Vector<String>();
		File f = new File(nazwaKatalogu);
		String[] pliki =  f.list();
		for (String plik : pliki)
		{
			plikiWKatalogu.add(plik);
		}
		return plikiWKatalogu;
	}
}