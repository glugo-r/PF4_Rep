package utilidades;

import java.util.Scanner;

public class EntradaUtils 
{
	
	public static int leerEntero(Scanner scanner) 
	{
        while (true) 
        {
            try 
            {
                return Integer.parseInt(scanner.nextLine());
            } 
            catch (NumberFormatException e) 
            {
                System.out.print("Ingrese un número válido: ");
            }
        }
    }
}