package usuarios;

import utilidades.EntradaUtils;
import java.util.Scanner;
import principal.SistemaTareas;


public class Sudo extends Usuario 
{
	private SistemaTareas sistema;
    private Scanner scanner;

    public Sudo(SistemaTareas sistema, Scanner scanner) 
    {
        super("SuperAdmin", "sudo@restaurante.com", "Sudo", "admin123"); // Contraseña por defecto
        this.sistema = sistema;
        this.scanner = scanner;
    }
    
    public Administrador crearAdministrador(String nombre, String email, String password) 
    {
        return new Administrador(nombre, email, password);
    }
    
    private boolean mostrarMenuSudo() 
    {
	    System.out.println("\n=== MENÚ SUDO ===");
	    System.out.println("1. Crear nuevo usuario");
	    System.out.println("2. Listar todos los usuarios");
	    System.out.println("3. Ver ventas del día");
	    System.out.println("4. Ver/eliminar tareas");
	    System.out.println("5. Ver mi información");
	    System.out.println("6. Cambiar mi contraseña");
	    System.out.println("7. Eliminar usuario");
	    System.out.println("8. Eliminar orden"); 
	    System.out.println("9. Cerrar sesión");
	    System.out.print("Seleccione opción: ");
	    
	    int opcion = EntradaUtils.leerEntero(scanner);
	    
	    switch (opcion) {
	        case 1:
	            crearNuevoUsuario();
	            System.out.println("\nPresione Enter para continuar...");
	            scanner.nextLine();
	            return false;
	        case 2:
	            System.out.println("\n¿Mostrar contraseñas? (s/n): ");
	            String respuesta = scanner.nextLine().toLowerCase();
	            boolean mostrarPasswords = respuesta.equals("s") || respuesta.equals("si");
	            sistema.listarUsuarios(mostrarPasswords);
	            System.out.println("\nPresione Enter para continuar...");
	            scanner.nextLine();
	            return false;
	        case 3:
	            System.out.println("Ventas del día: $" + sistema.getVentasDia());
	            System.out.println("\nPresione Enter para continuar...");
	            scanner.nextLine();
	            return false;
	        case 4:
	            gestionarTareasSudo();
	            System.out.println("\nPresione Enter para continuar...");
	            scanner.nextLine();
	            return false;
	        case 5:
	            sistema.getUsuarioActual().mostrarInfo(true);
	            System.out.println("\nPresione Enter para continuar...");
	            scanner.nextLine();
	            return false;
	        case 6:
	            cambiarPasswordSudo();
	            System.out.println("\nPresione Enter para continuar...");
	            scanner.nextLine();
	            return false;
	        case 7:
	            eliminarUsuarioSudo();
	            System.out.println("\nPresione Enter para continuar...");
	            scanner.nextLine();
	            return false;
	        case 8:
	            eliminarOrdenSudo();
	            System.out.println("\nPresione Enter para continuar...");
	            scanner.nextLine();
	            return false;
	        case 9:
	            sistema.setUsuarioActual(null);
	            System.out.println("Sesión cerrada correctamente.");
	            return true;
	        default:
	            System.out.println("Opción inválida");
	            System.out.println("\nPresione Enter para continuar...");
	            scanner.nextLine();
	            return false;
	    }
	}

}
