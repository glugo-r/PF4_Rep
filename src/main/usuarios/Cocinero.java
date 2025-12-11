package usuarios;

import restaurante.Platillo;
import tareas.Tarea;
import restaurante.ItemOrden;
import restaurante.Orden;

import java.util.List;
import java.util.Scanner;
import utilidades.EntradaUtils;

import database.DatabaseManager;
import principal.SistemaTareas;

public class Cocinero extends Empleado 
{
	private SistemaTareas sistema;
    private Scanner scanner;
    
    public void setSistema(SistemaTareas sistema) { this.sistema = sistema; }
    public void setScanner(Scanner scanner) { this.scanner = scanner; }
	
    private int platillosPreparados;
    
    public Cocinero(String nombre, String email, String password, SistemaTareas sistema, Scanner scanner) 
    {
        super(nombre, email, "Cocinero", password);
        this.platillosPreparados = 0;
        this.sistema = sistema;
        this.scanner = scanner;
    }
    
    // Método existente para una unidad
    public void marcarComidaLista(Orden orden, Platillo platillo) {
        orden.marcarPlatilloListo(platillo);
        platillosPreparados++;
        
        // Guardar estadística inmediatamente
        DatabaseManager.guardarEstadisticasCocinero(this);
        
        System.out.println("Unidad de '" + platillo.getNombre() + "' marcada como lista.");
    }
    
    // Nuevo método para múltiples unidades
    public void marcarComidaLista(Orden orden, Platillo platillo, int cantidad) {
        for (int i = 0; i < cantidad; i++) {
            orden.marcarPlatilloListo(platillo);
            platillosPreparados++;
        }
        
        // Guardar estadística inmediatamente
        DatabaseManager.guardarEstadisticasCocinero(this);
        
        System.out.println("" + cantidad + " unidad(es) de '" + platillo.getNombre() + "' marcada(s) como lista.");
    }
    
    public int getPlatillosPreparados() {
        return platillosPreparados;
    }
    
    // Método para setear platillos preparados (al cargar desde archivo)
    public void setPlatillosPreparados(int cantidad) {
        this.platillosPreparados = cantidad;
    }
    
    public boolean mostrarMenu() 
    {
    	mostrarNotificacionesPendientes();
        System.out.println("\n=== MENÚ COCINERO ===");
        System.out.println("Bienvenido, " + this.getNombre());
        System.out.println("Platillos preparados hoy: " + this.getPlatillosPreparados());
        System.out.println("\n1. Ver mis tareas asignadas");
        System.out.println("2. Ver órdenes pendientes (resumen)");
        System.out.println("3. Ver detalles de órdenes pendientes");
        System.out.println("4. Marcar platillo como listo");
        System.out.println("5. Ver mi información");
        System.out.println("6. Marcar tarea como completada");
        System.out.println("7. Cerrar sesión");
        System.out.print("Seleccione opción: ");
        
        int opcion = EntradaUtils.leerEntero(scanner);
        
        switch (opcion) {
            case 1:
                this.consultarTareas();
                System.out.println("\nPresione Enter para continuar...");
                scanner.nextLine();
                return false;
            case 2:
                verResumenOrdenesPendientes();
                System.out.println("\nPresione Enter para continuar...");
                scanner.nextLine();
                return false;
            case 3:
                verOrdenesPendientes();
                System.out.println("\nPresione Enter para continuar...");
                scanner.nextLine();
                return false;
            case 4:
                marcarPlatilloListo();
                System.out.println("\nPresione Enter para continuar...");
                scanner.nextLine();
                return false;
            case 5:
                this.mostrarInfo(false);
                System.out.println("\nPresione Enter para continuar...");
                scanner.nextLine();
                return false;
            case 7:
                sistema.setUsuarioActual(null);
                System.out.println("Sesión cerrada correctamente.");
                return true;
            case 6: 
                mostrarTareasPendientes(); // aquí conviene mostrar también el ID de cada tarea
                System.out.print("Ingrese el ID de la tarea a completar: ");
                int id = EntradaUtils.leerEntero(scanner);
                completarTareaPorId(id);
                System.out.println("\nPresione Enter para continuar...");
                scanner.nextLine();
                return false;
            default:
                System.out.println("Opción inválida");
                System.out.println("\nPresione Enter para continuar...");
                scanner.nextLine();
                return false;
        }
    }
    
    private void verOrdenesPendientes() {
        List<Orden> ordenesPendientes = sistema.getOrdenes().stream()
            .filter(o -> !o.isEntregada())
            .collect(java.util.stream.Collectors.toList());
        
        if (ordenesPendientes.isEmpty()) {
            System.out.println("No hay órdenes pendientes");
        } else {
            for (Orden orden : ordenesPendientes) {
                orden.mostrarOrden();
                System.out.println("===================");
            }
        }
    }
    
    private void verResumenOrdenesPendientes() 
    {
	    List<Orden> ordenesPendientes = sistema.getOrdenes().stream()
	        .filter(o -> !o.isEntregada())
	        .collect(java.util.stream.Collectors.toList());
	    
	    if (ordenesPendientes.isEmpty()) {
	        System.out.println(" No hay órdenes pendientes.");
	        return;
	    }
	    
	    System.out.println("\n=== RESUMEN DE ÓRDENES PENDIENTES ===");
	    System.out.println("Total órdenes: " + ordenesPendientes.size());
	    
	    for (Orden orden : ordenesPendientes) {
	        int totalPlatillos = orden.getTotalPlatillos();
	        int listos = orden.getCantidadPlatillosListos(); // Cambiado
	        int pendientes = orden.getCantidadPlatillosPendientes(); // Cambiado
	        
	        String estado = orden.estaLista() ? "LISTA" : "EN PROCESO";
	        
	        System.out.println("\nOrden #" + orden.getId() + 
	                         " | Mesa: " + orden.getMesa().getNumero() +
	                         " | " + estado +
	                         " | Platillos: " + listos + "/" + totalPlatillos);
	        
	        if (pendientes > 0) {
	            System.out.print("  Platillos pendientes: ");
	            List<ItemOrden> itemsPendientes = orden.getItemsPendientes();
	            for (ItemOrden item : itemsPendientes) {
	                System.out.print(item.getPlatillo().getNombre() + 
	                               " x" + item.getCantidadPendiente() + " ");
	            }
	            System.out.println();
	        }
	    }
	}

    private void marcarPlatilloListo() 
    {
	    // Mostrar órdenes pendientes
	    List<Orden> ordenesPendientes = sistema.getOrdenes().stream()
	        .filter(o -> !o.isEntregada())
	        .collect(java.util.stream.Collectors.toList());
	    
	    if (ordenesPendientes.isEmpty()) {
	        System.out.println("No hay órdenes pendientes para preparar.");
	        return;
	    }
	    
	    System.out.println("\n=== ÓRDENES PENDIENTES ===");
	    for (Orden orden : ordenesPendientes) {
	        System.out.println("\n[Orden #" + orden.getId() + "]");
	        System.out.println("Mesa: " + orden.getMesa().getNumero());
	        System.out.println("Mesero: " + orden.getMesero().getNombre());
	        System.out.println("Progreso: " + orden.getCantidadPlatillosListos() + "/" + orden.getTotalPlatillos() + " platillos listos");
	        System.out.println("-------------------");
	    }
	    
	    System.out.print("\nID de la orden a trabajar: ");
	    int idOrden = EntradaUtils.leerEntero(scanner);
	    
	    Orden orden = sistema.getOrdenes().stream()
	        .filter(o -> o.getId() == idOrden && !o.isEntregada())
	        .findFirst()
	        .orElse(null);
	    
	    if (orden == null) {
	        System.out.println(" Orden no encontrada o ya entregada.");
	        return;
	    }
	    
	    // Mostrar items de esta orden específica
	    System.out.println("\n=== ITEMS DE LA ORDEN #" + orden.getId() + " ===");
	    orden.mostrarParaCocinero();
	    
	    List<ItemOrden> itemsPendientes = orden.getItemsPendientes();
	    
	    if (itemsPendientes.isEmpty()) {
	        System.out.println("\n ¡Todos los items de esta orden ya están completos!");
	        System.out.println("Estado: LISTA PARA ENTREGAR");
	        return;
	    }
	    
	    System.out.println("\nSeleccione el platillo a marcar como listo:");
	    System.out.println("-------------------------------------------");
	    
	    int index = 1;
	    for (ItemOrden item : itemsPendientes) {
	        System.out.println(index + ". [ID: " + item.getPlatillo().getId() + "] " + 
	                         item.getPlatillo().getNombre() + 
	                         " - Pendientes: " + item.getCantidadPendiente() + "/" + item.getCantidad());
	        index++;
	    }
	    
	    System.out.print("\nSeleccione número del platillo (0 para cancelar): ");
	    int seleccion = EntradaUtils.leerEntero(scanner);
	    
	    if (seleccion == 0) {
	        System.out.println("Operación cancelada.");
	        return;
	    }
	    
	    if (seleccion < 1 || seleccion > itemsPendientes.size()) {
	        System.out.println(" Selección inválida.");
	        return;
	    }
	    
	    ItemOrden itemSeleccionado = itemsPendientes.get(seleccion - 1);
	    
	    // Preguntar cuántas unidades marcar como listas
	    System.out.println("\nPlatillo seleccionado: " + itemSeleccionado.getPlatillo().getNombre());
	    System.out.println("Cantidad pendiente: " + itemSeleccionado.getCantidadPendiente() + " de " + itemSeleccionado.getCantidad());
	    System.out.print("¿Cuántas unidades marcar como listas? (1-" + itemSeleccionado.getCantidadPendiente() + "): ");
	    int cantidad = EntradaUtils.leerEntero(scanner);
	    
	    if (cantidad < 1 || cantidad > itemSeleccionado.getCantidadPendiente()) {
	        System.out.println(" Cantidad inválida.");
	        return;
	    }
	    
	    // Usar el nuevo método que maneja cantidad
	    this.marcarComidaLista(orden, itemSeleccionado.getPlatillo(), cantidad);
	    
	    System.out.println("\n Marcadas " + cantidad + " unidad(es) de '" + 
	                     itemSeleccionado.getPlatillo().getNombre() + "' como LISTAS.");
	    
	    // Verificar si todos los items están completos
	    if (orden.estaLista()) {
	        System.out.println("\n ¡¡¡TODOS LOS ITEMS DE LA ORDEN #" + 
	                         orden.getId() + " ESTÁN COMPLETOS!!!");
	        System.out.println(" Informar al mesero que la orden está lista para entregar.");
	        System.out.println("Mesa: " + orden.getMesa().getNumero());
	        System.out.println("Mesero asignado: " + orden.getMesero().getNombre());
	    } else {
	        int pendientes = orden.getCantidadPlatillosPendientes();
	        int total = orden.getTotalPlatillos();
	        System.out.println(" Progreso: " + (total - pendientes) + "/" + 
	                         total + " platillos listos");
	        System.out.println(" Aún faltan " + pendientes + " platillo(s) por preparar.");
	    }
	    
	    // Guardar estado del sistema
	    sistema.guardarEstado();
	}

}
