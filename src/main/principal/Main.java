package principal;
import usuarios.*;

import notificaciones.NotificadorTareas;
import utilidades.EntradaUtils;
import java.util.*;

public class Main 
{
    private static SistemaTareas sistema;
    private static Scanner scanner;
    private static NotificadorTareas notificador;
    
    public static void main(String[] args) {
        sistema = new SistemaTareas();
        scanner = new Scanner(System.in);

        // Inyectar dependencias en los usuarios cargados
        for (Usuario u : sistema.getUsuarios()) {
            if (u instanceof Sudo sudo) {
                sudo.setSistema(sistema);
                sudo.setScanner(scanner);
            } else if (u instanceof Administrador admin) {
                admin.setSistema(sistema);
                admin.setScanner(scanner);
            } else if (u instanceof Cocinero coc) {
                coc.setSistema(sistema);
                coc.setScanner(scanner);
            } else if (u instanceof Mesero mes) {
                mes.setSistema(sistema);
                mes.setScanner(scanner);
            }
        }
        
        // Iniciar hilo de notificaciones
        notificador = new NotificadorTareas(sistema.getTareas());
        notificador.start();
        
        sistema.reconstruirAsignaciones(); // Para reconstruir las tareas asignadas a cada empleado
        
        // Agregar shutdown hook para guardar automáticamente
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.out.println("\n Guardando estado del sistema...");
            sistema.guardarEstado();
            notificador.detener();
            System.out.println(" Sistema cerrado correctamente.");
        }));
        
        System.out.println("=== SISTEMA DE GESTIÓN DE RESTAURANTE ===");
        mostrarMenuPrincipal();
        
        sistema.guardarEstado();
        scanner.close();
        notificador.detener();
    }
    
    private static void mostrarMenuPrincipal() {
        boolean salir = false;
        
        while (!salir) {
            System.out.println("\n=== MENÚ PRINCIPAL ===");
            System.out.println("1. Iniciar sesión");
            System.out.println("2. Salir");
            System.out.print("Seleccione opción: ");
            
            int opcion = EntradaUtils.leerEntero(scanner);
            
            switch (opcion) {
                case 1:
                    iniciarSesion();
                    break;
                case 2:
                    salir = true;
                    System.out.println("Saliendo del sistema...");
                    break;
                default:
                    System.out.println("Opción inválida");
            }
        }
    }
    
    private static void iniciarSesion() {
        System.out.print("Ingrese email: ");
        String email = scanner.nextLine();
        
        System.out.print("Ingrese contraseña: ");
        String password = scanner.nextLine();
        
        // Autenticar usuario
        Usuario usuario = sistema.autenticarUsuario(email, password);
        
        if (usuario != null) {
            sistema.setUsuarioActual(usuario);
            System.out.println("¡Bienvenido, " + usuario.getNombre() + "!");
            mostrarMenuSegunRol();
        } else {
            System.out.println("Credenciales incorrectas. Intente nuevamente.");
        }
    }
    
    private static void mostrarMenuSegunRol() {
        Usuario usuario = sistema.getUsuarioActual();
        boolean cerrarSesion = false;
        
        while (!cerrarSesion) {
            
            if (usuario instanceof Sudo)
                cerrarSesion = ((Sudo) usuario).mostrarMenu();
            else if (usuario instanceof Administrador) {
                cerrarSesion = ((Administrador) usuario).mostrarMenu();
            } else if (usuario instanceof Cocinero) {
                cerrarSesion = ((Cocinero) usuario).mostrarMenu();
            } else if (usuario instanceof Mesero) {
                cerrarSesion = ((Mesero) usuario).mostrarMenu();
            } else {
                System.out.println("Rol no reconocido");
                cerrarSesion = true;
            }
        }
    }
    
}
