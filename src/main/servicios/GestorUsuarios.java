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

        int tipo;
        while (true) 
        {
            System.out.print("Seleccione tipo: ");
            tipo = EntradaUtils.leerEntero(scanner);
            if (tipo >= 1 && tipo <= 3) break;
            System.out.println(" Error: opción inválida. Intente de nuevo.");
        }

        // Validar nombre con reintentos
        String nombre;
        while (true) 
        {
            System.out.print("Nombre: ");
            nombre = scanner.nextLine();
            try {
                sistema.validarNombre(nombre);
                break;
            } catch (NombreInvalidoException e) {
                System.out.println(" Error: " + e.getMessage() + ". Intente de nuevo.");
            }
        }

        // Validar email con reintentos
        String email;
        while (true) 
        {
            System.out.print("Email (debe terminar en .com): ");
            email = scanner.nextLine();
            try {
                sistema.validarEmail(email);
                break;
            } catch (EmailInvalidoException e) {
                System.out.println(" Error: " + e.getMessage() + ". Intente de nuevo.");
            }
        }

        // Contraseña (sin validación especial, pero vemos más adelante si ponemos o no)
        System.out.print("Contraseña: ");
        String password = scanner.nextLine();

        // Crear usuario según tipo
        try 
        {
            switch (tipo) {
                case 1:
                    sistema.agregarUsuario(new Administrador(nombre, email, password, sistema, scanner));
                    System.out.println(" Administrador creado exitosamente");
                    break;
                case 2:
                    sistema.agregarUsuario(new Cocinero(nombre, email, password, sistema, scanner));
                    System.out.println(" Cocinero creado exitosamente");
                    break;
                case 3:
                    sistema.agregarUsuario(new Mesero(nombre, email, password, sistema, scanner));
                    System.out.println(" Mesero creado exitosamente");
                    break;
                default:
                    System.out.println(" Tipo de usuario inválido");
                    return;
            }
        } 
        catch (NombreInvalidoException | EmailInvalidoException e) 
        {
            System.out.println(" Error: " + e.getMessage());
        }
    }
	
}
