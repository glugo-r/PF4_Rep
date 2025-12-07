package usuarios;

import utilidades.EntradaUtils;
import java.util.Scanner;
import principal.SistemaTareas;


public class Sudo extends Usuario 
{
	private SistemaTareas sistema;
    private Scanner scanner;

    public Sudo() 
    {
        super("SuperAdmin", "sudo@restaurante.com", "Sudo", "admin123"); // Contraseña por defecto
        this.sistema = sistema;
        this.scanner = scanner;
    }
    
    public Administrador crearAdministrador(String nombre, String email, String password) 
    {
        return new Administrador(nombre, email, password);
    }
}
