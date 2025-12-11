package usuarios;

import utilidades.EntradaUtils;
import servicios.GestorUsuarios;
import servicios.GestorLogs;
import servicios.GestorTareas;

import java.util.List;
import java.util.Scanner;

import database.DatabaseManager;
import principal.SistemaTareas;
import restaurante.Orden;


public class Sudo extends Usuario 
{
	private SistemaTareas sistema;
    private Scanner scanner;
    
    public void setSistema(SistemaTareas sistema) { this.sistema = sistema; }
    public void setScanner(Scanner scanner) { this.scanner = scanner; }

    public Sudo(SistemaTareas sistema, Scanner scanner) 
    {
        super("SuperAdmin", "sudo@restaurante.com", "Sudo", "admin123"); // Contraseña por defecto
        this.sistema = sistema;
        this.scanner = scanner;
    }
    
    public Administrador crearAdministrador(String nombre, String email, String password) 
    {
        return new Administrador(nombre, email, password, sistema, scanner);
    }
    
    public boolean mostrarMenu() 
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
		        // Crear un gestor de usuarios con las dependencias ya inyectadas
		        GestorUsuarios gestor = new GestorUsuarios(sistema, scanner);
		        gestor.crearNuevoUsuario();   // Llamada al método del gestor
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
	            System.out.println("Ventas del día: $" + String.format("%.2f", sistema.getVentasDia()));
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
    
    private void gestionarTareasSudo() 
    {
        System.out.println("\n=== GESTIÓN DE TAREAS ===");
        System.out.println("1. Listar todas las tareas");
        System.out.println("2. Eliminar tarea");
        System.out.print("Seleccione opción: ");
        
        int opcion = EntradaUtils.leerEntero(scanner);
        
        switch (opcion) 
        {
            case 1:
                sistema.listarTareas();
                break;
            case 2:
            	GestorTareas gestorTareas = new GestorTareas(sistema, scanner);
            	gestorTareas.eliminarTarea();
                break;
            default:
                System.out.println("Opción inválida");
        }
    }
    
    private void cambiarPasswordSudo() 
    {
        System.out.print("Ingrese nueva contraseña: ");
        String nuevaPassword = scanner.nextLine();
        
        System.out.print("Confirme nueva contraseña: ");
        String confirmPassword = scanner.nextLine();
        
        if (nuevaPassword.equals(confirmPassword)) {
            if (nuevaPassword.length() < 4) 
            {
                System.out.println("La contraseña debe tener al menos 4 caracteres.");
                return;
            }
            
            sistema.getUsuarioActual().setPassword(nuevaPassword);
            DatabaseManager.guardarUsuarios(sistema.getUsuarios());
            System.out.println("Contraseña cambiada exitosamente.");
        } 
        else
            System.out.println("Las contraseñas no coinciden.");
    }

    private void eliminarUsuarioSudo() 
    {
	    System.out.println("\n=== ELIMINAR USUARIO ===");
	    sistema.listarUsuarios(false);
	    
	    System.out.print("\nID del usuario a eliminar (0 para cancelar): ");
	    int idUsuario = EntradaUtils.leerEntero(scanner);
	    
	    if (idUsuario == 0) {
	        System.out.println("Operación cancelada.");
	        return;
	    }
	    
	    if (idUsuario == 1) {
	        System.out.println("No se puede eliminar al usuario Sudo (ID: 1).");
	        return;
	    }
	    
	    Usuario usuario = sistema.buscarUsuarioPorId(idUsuario);
	    if (usuario == null) {
	        System.out.println("Usuario no encontrado.");
	        return;
	    }
	    
	    System.out.println("Usuario a eliminar:");
	    usuario.mostrarInfo(false);
	    
	    System.out.print("\n¿Está seguro de eliminar este usuario? (s/n): ");
	    String confirmacion = scanner.nextLine().toLowerCase();
	    
	    if (confirmacion.equals("s") || confirmacion.equals("si")) {
	        sistema.eliminarUsuario(idUsuario);
	        System.out.println("Usuario eliminado exitosamente.");
	    } else {
	        System.out.println("Operación cancelada.");
	    }
	}
    
    private void eliminarOrdenSudo() 
    {
        System.out.println("\n=== ELIMINAR ORDEN ===");
        
        // Mostrar todas las órdenes
        List<Orden> todasLasOrdenes = sistema.getOrdenes();
        
        if (todasLasOrdenes.isEmpty()) {
            System.out.println("No hay órdenes registradas en el sistema.");
            return;
        }
        
        System.out.println("Lista de todas las órdenes:");
        System.out.println("===========================");
        
        for (Orden orden : todasLasOrdenes) {
            System.out.println("\n[Orden #" + orden.getId() + "]");
            System.out.println("Mesa: " + orden.getMesa().getNumero());
            System.out.println("Mesero: " + orden.getMesero().getNombre());
            System.out.println("Fecha: " + orden.getFecha());
            System.out.println("Total: $" + orden.getTotal());
            System.out.println("Estado: " + (orden.isEntregada() ? " ENTREGADA" : "ACTIVA"));
            System.out.println("Lista: " + (orden.estaLista() ? "SÍ" : "NO"));
            System.out.println("-------------------");
        }
        
        System.out.print("\nID de la orden a eliminar (0 para cancelar): ");
        int idOrden = EntradaUtils.leerEntero(scanner);
        
        if (idOrden == 0) {
            System.out.println("Operación cancelada.");
            return;
        }
        
        // Buscar la orden
        Orden orden = sistema.buscarOrdenPorId(idOrden);
        
        if (orden == null) {
            System.out.println("Orden no encontrada.");
            return;
        }
        
        // Mostrar detalles completos de la orden
        System.out.println("\n=== DETALLES DE LA ORDEN A ELIMINAR ===");
        orden.mostrarOrden();
        
        // Preguntar motivo
        System.out.println("\nADVERTENCIA: Esta acción no se puede deshacer");
        System.out.println("Motivos comunes para eliminar órdenes:");
        System.out.println("1. Error al tomar la orden");
        System.out.println("2. Cliente canceló el pedido");
        System.out.println("3. Problema con el pago");
        System.out.println("4. Otra razón");
        
        System.out.print("\nSeleccione motivo (1-4): ");
        int motivo = EntradaUtils.leerEntero(scanner);
        
        String[] motivos = {
            "Error al tomar la orden",
            "Cliente canceló el pedido", 
            "Problema con el pago",
            "Otra razón"
        };
        
        String motivoStr = (motivo >= 1 && motivo <= 4) ? motivos[motivo-1] : "No especificado";
        
        System.out.println("\nMotivo registrado: " + motivoStr);
        System.out.print("\n¿Está SEGURO de eliminar esta orden? (escriba 'ELIMINAR' para confirmar): ");
        String confirmacion = scanner.nextLine();
        
        if (!confirmacion.equalsIgnoreCase("ELIMINAR")) {
            System.out.println("Eliminación cancelada. La orden NO fue eliminada.");
            return;
        }
        
        // Eliminar la orden
        boolean eliminada = sistema.eliminarOrden(idOrden);
        
        if (eliminada) {
            System.out.println("Orden eliminada exitosamente.");
            
            // Registrar la eliminación en un log
            GestorLogs.registrarLogEliminacionOrden(idOrden, motivoStr, sistema.getUsuarioActual().getNombre());
        }
    }

}
