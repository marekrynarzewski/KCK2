package qa;

import java.io.File;

class Katalog
{
	public static boolean utworz(String nazwaKatalogu)
	{
		File f = new File(nazwaKatalogu);
		return f.mkdir();
	}
}