package qa;

import java.util.Scanner;

public class Ekran
{

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
		wypiszZLinia("[Błąd] " + wiadomosc);
		System.exit(numerBledu);
	}
	
	public static void ostrzez(String wiadomosc)
	{
		wypiszZLinia("[Ostrzeżenie] " + wiadomosc);
	}
	
	public static void info(String wiadomosc)
	{
		wypiszZLinia("[Informacja] " + wiadomosc);
	}
	
	public static void wyjscie(int numerBledu)
	{
		System.exit(numerBledu);
	}

	public static Scanner wejscie = new Scanner(System.in);
	
	
}
