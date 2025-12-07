package servicios;

import utilidades.EntradaUtils;
import principal.SistemaTareas;
import java.util.Scanner;

import excepciones.EmailInvalidoException;
import excepciones.NombreInvalidoException;
import usuarios.Administrador;
import usuarios.Cocinero;
import usuarios.Mesero;

public class GestorUsuarios 
{
	private SistemaTareas sistema;
    private Scanner scanner;

    public GestorUsuarios(SistemaTareas sistema, Scanner scanner) {
        this.sistema = sistema;
        this.scanner = scanner;
    }
	
	public void crearNuevoUsuario() 
	{
	    System.out.println("\n=== CREAR NUEVO USUARIO ===");
	    System.out.println("Tipo de usuario:");
	    System.out.println("1. Administrador");
	    System.out.println("2. Cocinero");
	    System.out.println("3. Mesero");
	    System.out.print("Seleccione tipo: ");
	    
	    int tipo = EntradaUtils.leerEntero(scanner);
	    
	    System.out.print("Nombre: ");
	    String nombre = scanner.nextLine();
	    
	    System.out.print("Email (debe terminar en .com): ");
	    String email = scanner.nextLine();
	    
	    System.out.print("Contraseña: ");
	    String password = scanner.nextLine();
	    
	    try {
	        switch (tipo) {
	            case 1:
	                sistema.agregarUsuario(new Administrador(nombre, email, password, sistema, scanner));
	                System.out.println(" Administrador creado exitosamente");
	                break;
	            case 2:
	                sistema.agregarUsuario(new Cocinero(nombre, email, password));
	                System.out.println(" Cocinero creado exitosamente");
	                break;
	            case 3:
	                sistema.agregarUsuario(new Mesero(nombre, email, password));
	                System.out.println(" Mesero creado exitosamente");
	                break;
	            default:
	                System.out.println(" Tipo de usuario inválido");
	                return;
	        }
	    } catch (EmailInvalidoException | NombreInvalidoException e) {
	        System.out.println(" Error: " + e.getMessage());
	    }
	}
	
	

}
