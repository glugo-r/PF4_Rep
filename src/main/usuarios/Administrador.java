package usuarios;

import utilidades.EntradaUtils;
import servicios.GestorLogs;
import servicios.GestorUsuarios;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Scanner;

import excepciones.FechaInvalidaException;
import principal.SistemaTareas;
import restaurante.Orden;
import tareas.Tarea;

public class Administrador extends Usuario 
{
	private SistemaTareas sistema;
    private Scanner scanner;
    
    public void setSistema(SistemaTareas sistema) { this.sistema = sistema; }
    public void setScanner(Scanner scanner) { this.scanner = scanner; }
    
    public Administrador(String nombre, String email, String password, SistemaTareas sistema, Scanner scanner) 
    {
        super(nombre, email, "Administrador", password);
        this.sistema = sistema;
        this.scanner = scanner;
    }
    
    public Tarea crearTarea(String titulo, String descripcion, String fechaLimite) {
        return new Tarea(titulo, descripcion, fechaLimite);
    }
    
    public void asignarTarea(Tarea tarea, Empleado empleado) {
        tarea.setUsuarioAsignado(empleado);
        empleado.agregarTarea(tarea);
    }
    
    public void listarUsuarios(List<Usuario> usuarios) {
        listarUsuarios(usuarios, false);
    }
    
    public void listarUsuarios(List<Usuario> usuarios, boolean mostrarPasswords) {
        for (Usuario usuario : usuarios) {
            usuario.mostrarInfo(mostrarPasswords);
            System.out.println("-------------------");
        }
    }
    
    public void listarTareas(List<Tarea> tareas) {
        for (Tarea tarea : tareas) {
            tarea.mostrarDetalles();
            System.out.println("-------------------");
        }
    }
    
    public boolean mostrarMenu() 
    {
	    System.out.println("\n=== MENÚ ADMINISTRADOR ===");
	    System.out.println("1. Crear tarea");
	    System.out.println("2. Asignar tarea");
	    System.out.println("3. Listar tareas");
	    System.out.println("4. Eliminar tarea");
	    System.out.println("5. Listar empleados");
	    System.out.println("6. Agregar nuevo empleado");
	    System.out.println("7. Eliminar empleado");
	    System.out.println("8. Eliminar orden");
	    System.out.println("9. Ver mi información");
	    System.out.println("10. Cerrar sesión");
	    System.out.print("Seleccione opción: ");
	    
	    int opcion = EntradaUtils.leerEntero(scanner);
	    
	    switch (opcion) {
	        case 1:
	            crearTarea();
	            System.out.println("\nPresione Enter para continuar...");
	            scanner.nextLine();
	            return false;
	        case 2:
	            asignarTarea();
	            System.out.println("\nPresione Enter para continuar...");
	            scanner.nextLine();
	            return false;
	        case 3:
	            sistema.listarTareas();
	            System.out.println("\nPresione Enter para continuar...");
	            scanner.nextLine();
	            return false;
	        case 4:
	            eliminarTarea();
	            System.out.println("\nPresione Enter para continuar...");
	            scanner.nextLine();
	            return false;
	        case 5:
	            listarEmpleados();
	            System.out.println("\nPresione Enter para continuar...");
	            scanner.nextLine();
	            return false;
	        case 6: // Agregar nuevo empleado
	            GestorUsuarios gestor = new GestorUsuarios(sistema, scanner);
	            gestor.crearNuevoUsuario();   // delega directamente al gestor
	            System.out.println("\nPresione Enter para continuar...");
	            scanner.nextLine();
	            return false;
	        case 7:
	            eliminarEmpleadoAdministrador();
	            System.out.println("\nPresione Enter para continuar...");
	            scanner.nextLine();
	            return false;
	        case 8:
	            eliminarOrdenAdministrador();
	            System.out.println("\nPresione Enter para continuar...");
	            scanner.nextLine();
	            return false;
	        case 9:
	            this.mostrarInfo(false);
	            System.out.println("\nPresione Enter para continuar...");
	            scanner.nextLine();
	            return false;
	        case 10:
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
    
    private void eliminarEmpleadoAdministrador() 
    {
	    System.out.println("\n=== ELIMINAR EMPLEADO ===");
	    
	    // Listar solo empleados (no administradores)
	    List<Empleado> empleados = sistema.getEmpleados();
	    
	    if (empleados.isEmpty()) {
	        System.out.println("No hay empleados registrados.");
	        return;
	    }
	    
	    System.out.println("Lista de empleados:");
	    for (Empleado empleado : empleados) {
	        System.out.println("ID: " + empleado.getId() + 
	                         " | Nombre: " + empleado.getNombre() + 
	                         " | Rol: " + empleado.getRol());
	    }
	    
	    System.out.print("\nID del empleado a eliminar (0 para cancelar): ");
	    int idEmpleado = EntradaUtils.leerEntero(scanner);
	    
	    if (idEmpleado == 0) {
	        System.out.println("Operación cancelada.");
	        return;
	    }
	    
	    // Verificar que sea un empleado (no administrador o sudo)
	    Usuario usuario = sistema.buscarUsuarioPorId(idEmpleado);
	    
	    if (usuario == null) {
	        System.out.println("Usuario no encontrado.");
	        return;
	    }
	    
	    // Verificar que no sea administrador o sudo
	    if (usuario instanceof Administrador || usuario instanceof Sudo) {
	        System.out.println("No tiene permisos para eliminar administradores.");
	        System.out.println("Solo el Sudo puede eliminar administradores.");
	        return;
	    }
	    
	    if (!(usuario instanceof Empleado)) {
	        System.out.println("El usuario seleccionado no es un empleado.");
	        return;
	    }
	    
	    System.out.println("Empleado a eliminar:");
	    usuario.mostrarInfo(false);
	    
	    System.out.print("\n¿Está seguro de eliminar este empleado? (s/n): ");
	    String confirmacion = scanner.nextLine().toLowerCase();
	    
	    if (confirmacion.equals("s") || confirmacion.equals("si")) {
	        sistema.eliminarUsuario(idEmpleado);
	        System.out.println("Empleado eliminado exitosamente.");
	    } else {
	        System.out.println("Operación cancelada.");
	    }
	}
    
    private void eliminarOrdenAdministrador() 
    {
        System.out.println("\n=== ELIMINAR ORDEN (ADMINISTRADOR) ===");
        
        // Mostrar solo órdenes activas (no entregadas)
        List<Orden> ordenesActivas = sistema.getOrdenes().stream()
            .filter(o -> !o.isEntregada())
            .collect(java.util.stream.Collectors.toList());
        
        if (ordenesActivas.isEmpty()) {
            System.out.println("No hay órdenes activas para eliminar.");
            return;
        }
        
        System.out.println("Órdenes activas (no entregadas):");
        System.out.println("=================================");
        
        for (Orden orden : ordenesActivas) {
            System.out.println("\n[Orden #" + orden.getId() + "]");
            System.out.println("Mesa: " + orden.getMesa().getNumero());
            System.out.println("Mesero: " + orden.getMesero().getNombre());
            System.out.println("Total: $" + orden.getTotal());
            System.out.println("Estado: " + (orden.estaLista() ? "LISTA" : "EN PREPARACIÓN"));
            System.out.println("Progreso: " + orden.getCantidadPlatillosListos() + "/" + 
                             orden.getTotalPlatillos() + " platillos listos");
            System.out.println("-------------------");
        }
        
        System.out.print("\nID de la orden a eliminar (0 para cancelar): ");
        int idOrden = EntradaUtils.leerEntero(scanner);
        
        if (idOrden == 0) {
            System.out.println("Operación cancelada.");
            return;
        }
        
        // Verificar que la orden exista y no esté entregada
        Orden orden = sistema.buscarOrdenPorId(idOrden);
        
        if (orden == null) {
            System.out.println(" Orden no encontrada.");
            return;
        }
        
        if (orden.isEntregada()) {
            System.out.println(" No tiene permisos para eliminar órdenes ya entregadas.");
            System.out.println("   Solo el Sudo puede eliminar órdenes entregadas.");
            return;
        }
        
        // Mostrar detalles
        System.out.println("\n=== DETALLES DE LA ORDEN ===");
        orden.mostrarOrden();
        
        System.out.print("\n¿Está seguro de eliminar esta orden? (s/n): ");
        String respuesta = scanner.nextLine().toLowerCase();
        
        if (!respuesta.equals("s") && !respuesta.equals("si")) {
            System.out.println("Eliminación cancelada.");
            return;
        }
        
        // Preguntar motivo breve
        System.out.print("Motivo breve de eliminación: ");
        String motivo = scanner.nextLine();
        
        // Eliminar la orden
        boolean eliminada = sistema.eliminarOrden(idOrden);
        
        if (eliminada) {
            System.out.println(" Orden eliminada exitosamente.");
            
            // Registrar log
            GestorLogs.registrarLogEliminacionOrden(idOrden, "Admin: " + motivo, sistema.getUsuarioActual().getNombre());
            
            // Notificar al mesero si es posible
            System.out.println(" Informar al mesero " + orden.getMesero().getNombre() + 
                             " sobre la eliminación de la orden #" + idOrden);
        }
    }
    
    private void crearTarea() 
    {
	    System.out.print("Título de la tarea: ");
	    String titulo = scanner.nextLine();
	    
	    System.out.print("Descripción: ");
	    String descripcion = scanner.nextLine();
	    
	    // Solicitar fecha con formato específico
	    System.out.print("Fecha límite (Formato: yyyy-MM-dd HH:mm, ejemplo: 2025-12-31 18:30): ");
	    String fechaLimite = scanner.nextLine();
	    
	    // Validar formato de fecha
	    if (!validarFormatoFecha(fechaLimite)) {
	        System.out.println(" Error: El formato de fecha debe ser yyyy-MM-dd HH:mm");
	        System.out.println("   Ejemplo: 2025-12-31 18:30");
	        return;
	    }
	    
	    Tarea tarea = this.crearTarea(titulo, descripcion, fechaLimite);
	    try {
	        sistema.agregarTarea(tarea);
	        System.out.println(" Tarea creada exitosamente");
	    } catch (FechaInvalidaException e) {
	        System.out.println(" Error al crear tarea: " + e.getMessage());
	    }
	}
    
    // Método para validar el formato de fecha
    private static boolean validarFormatoFecha(String fecha) 
    {
        try {
            // Intentar parsear la fecha
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
            sdf.setLenient(false); // No permitir fechas inválidas como 2024-02-30
            Date fechaParseada = sdf.parse(fecha);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    private void asignarTarea() 
    {
	    System.out.println("\n=== ASIGNAR TAREA ===");
	    
	    if (sistema.getTareas().isEmpty()) {
	        System.out.println("No hay tareas disponibles para asignar.");
	        System.out.println("Primero debe crear tareas.");
	        return;
	    }
	    
	    System.out.println("Tareas disponibles:");
	    sistema.listarTareas();
	    
	    System.out.print("\nID de la tarea a asignar (0 para cancelar): ");
	    int idTarea = EntradaUtils.leerEntero(scanner);
	    
	    if (idTarea == 0) {
	        System.out.println("Operación cancelada.");
	        return;
	    }
	    
	    Tarea tarea = sistema.buscarTareaPorId(idTarea);
	    if (tarea == null) {
	        System.out.println("Tarea no encontrada");
	        return;
	    }
	    
	    // Mostrar empleados disponibles
	    List<Empleado> empleados = sistema.getEmpleados();
	    if (empleados.isEmpty()) {
	        System.out.println(" No hay empleados registrados para asignar tareas.");
	        return;
	    }
	    
	    System.out.println("\nEmpleados disponibles:");
	    for (Empleado emp : empleados) {
	        System.out.println("ID: " + emp.getId() + 
	                         " | Nombre: " + emp.getNombre() + 
	                         " | Rol: " + emp.getRol() +
	                         " | Tareas asignadas: " + emp.getTareasAsignadas().size());
	    }
	    
	    System.out.print("\nID del empleado a asignar (0 para cancelar): ");
	    int idEmpleado = EntradaUtils.leerEntero(scanner);
	    
	    if (idEmpleado == 0) {
	        System.out.println("Operación cancelada.");
	        return;
	    }
	    
	    Empleado empleado = sistema.getEmpleados().stream()
	        .filter(e -> e.getId() == idEmpleado)
	        .findFirst()
	        .orElse(null);
	    
	    if (empleado != null) {
	        // Si la tarea ya tenía asignado a alguien, removerla
	        if (tarea.getUsuarioAsignado() != null) {
	            tarea.getUsuarioAsignado().removerTarea(tarea);
	            System.out.println("  Tarea reasignada. Anterior asignado: " + 
	                             tarea.getUsuarioAsignado().getNombre());
	        }
	        
	        this.asignarTarea(tarea, empleado);
	        System.out.println(" Tarea '" + tarea.getTitulo() + 
	                         "' asignada exitosamente a " + empleado.getNombre());
	    } else {
	        System.out.println(" Empleado no encontrado");
	    }
	}

    private void listarEmpleados() 
    {
        List<Empleado> empleados = sistema.getEmpleados();
        if (empleados.isEmpty()) {
            System.out.println("No hay empleados registrados");
        } else {
            for (Empleado emp : empleados) {
                emp.mostrarInfo(false);
                System.out.println("-------------------");
            }
        }
    }
      
    private void eliminarTarea() 
    {
        System.out.println("\n=== ELIMINAR TAREA ===");
        
        if (sistema.getTareas().isEmpty()) {
            System.out.println("No hay tareas registradas.");
            return;
        }
        
        System.out.println("Lista de tareas:");
        sistema.listarTareas();
        
        System.out.print("\nID de la tarea a eliminar (0 para cancelar): ");
        int idTarea = EntradaUtils.leerEntero(scanner);
        
        if (idTarea == 0) {
            System.out.println("Operación cancelada.");
            return;
        }
        
        Tarea tarea = sistema.buscarTareaPorId(idTarea);
        if (tarea == null) {
            System.out.println("Tarea no encontrada.");
            return;
        }
        
        System.out.println("Tarea a eliminar:");
        tarea.mostrarDetalles();
        
        System.out.print("\n¿Está seguro de eliminar esta tarea? (s/n): ");
        String confirmacion = scanner.nextLine().toLowerCase();
        
        if (confirmacion.equals("s") || confirmacion.equals("si")) {
            // Remover tarea de cualquier empleado asignado
            if (tarea.getUsuarioAsignado() != null) {
                tarea.getUsuarioAsignado().removerTarea(tarea);
            }
            
            // Eliminar tarea del sistema
            sistema.getTareas().removeIf(t -> t.getId() == idTarea);
            System.out.println("Tarea eliminada exitosamente.");
        } else {
            System.out.println("Operación cancelada.");
        }
    }

}
