package usuarios;

import restaurante.Platillo;
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
	
    private int platillosPreparados;
    
    public Cocinero(String nombre, String email, String password) {
        super(nombre, email, "Cocinero", password);
        this.platillosPreparados = 0;
    }
    
    // Método existente para una unidad
    public void marcarComidaLista(Orden orden, Platillo platillo) {
        orden.marcarPlatilloListo(platillo);
        platillosPreparados++;
        
        // Guardar estadística inmediatamente
        DatabaseManager.guardarEstadisticasCocinero(this);
        
        System.out.println("✅ Unidad de '" + platillo.getNombre() + "' marcada como lista.");
    }
    
    // Nuevo método para múltiples unidades
    public void marcarComidaLista(Orden orden, Platillo platillo, int cantidad) {
        for (int i = 0; i < cantidad; i++) {
            orden.marcarPlatilloListo(platillo);
            platillosPreparados++;
        }
        
        // Guardar estadística inmediatamente
        DatabaseManager.guardarEstadisticasCocinero(this);
        
        System.out.println("✅ " + cantidad + " unidad(es) de '" + platillo.getNombre() + "' marcada(s) como lista.");
    }
    
    public int getPlatillosPreparados() {
        return platillosPreparados;
    }
    
    // Método para setear platillos preparados (al cargar desde archivo)
    public void setPlatillosPreparados(int cantidad) {
        this.platillosPreparados = cantidad;
    }
    
    public boolean mostrarMenu() {
        System.out.println("\n=== MENÚ COCINERO ===");
        System.out.println("Bienvenido, " + this.getNombre());
        System.out.println("Platillos preparados hoy: " + this.getPlatillosPreparados());
        System.out.println("\n1. Ver mis tareas asignadas");
        System.out.println("2. Ver órdenes pendientes (resumen)");
        System.out.println("3. Ver detalles de órdenes pendientes");
        System.out.println("4. Marcar platillo como listo");
        System.out.println("5. Ver mi información");
        System.out.println("6. Cerrar sesión");
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
            case 6:
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
	        
	        String estado = orden.estaLista() ? "✅ LISTA" : "🔄 EN PROCESO";
	        
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

}
