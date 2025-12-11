package utilidades;

import java.util.NoSuchElementException;
import java.util.Scanner;

public class EntradaUtils 
{
	
	public static int leerEntero(Scanner scanner) 
	{
	    while (true) 
	    {
	        try 
	        {
	            return Integer.parseInt(scanner.nextLine().trim());
	        } 
	        catch (NumberFormatException e) 
	        {
	            System.out.println("Ingrese un número válido:");
	        } 
	        catch (NoSuchElementException e) 
	        {
	            System.out.println("Entrada cerrada inesperadamente.");
	            return -1; // o salir del programa
	        }
	    }
	}
}