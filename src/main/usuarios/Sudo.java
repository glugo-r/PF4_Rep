package usuarios;

public class Sudo extends Usuario {
    
    public Sudo() {
        super("SuperAdmin", "sudo@restaurante.com", "Sudo", "admin123"); // Contraseña por defecto
    }
    
    public Administrador crearAdministrador(String nombre, String email, String password) {
        return new Administrador(nombre, email, password);
    }
}
