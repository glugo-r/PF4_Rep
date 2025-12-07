package usuarios;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import servicios.GestorLogs;

import principal.SistemaTareas;
import restaurante.*;
import utilidades.EntradaUtils;

public class Mesero extends Empleado 
{
	private SistemaTareas sistema;
    private Scanner scanner;
	
    private int mesasAtendidas;
    private List<Orden> ordenesActivas;
    
    public Mesero(String nombre, String email, String password, SistemaTareas sistema, Scanner scanner) 
    {
        super(nombre, email, "Mesero", password);
        this.mesasAtendidas = 0;
        this.ordenesActivas = new ArrayList<>();
        this.sistema = sistema;
        this.scanner = scanner;
    }
    
    // Método principal que usa ItemOrden
    public Orden tomarPedido(Mesa mesa, List<ItemOrden> items) {
        Orden orden = new Orden(mesa, this);
        for (ItemOrden item : items) {
            orden.agregarItem(item);
        }
        ordenesActivas.add(orden);
        mesasAtendidas++;
        return orden;
    }
    
    // Método sobrecargado para compatibilidad (usa otro nombre)
    public Orden tomarPedidoConPlatillos(Mesa mesa, List<Platillo> platillos) {
        List<ItemOrden> items = new ArrayList<>();
        for (Platillo platillo : platillos) {
            items.add(new ItemOrden(platillo, 1)); // Cantidad 1 por defecto
        }
        return tomarPedido(mesa, items);
    }
    
    public void entregarPedido(Orden orden) {
        if (orden.estaLista()) {
            System.out.println("Pedido entregado a la mesa " + orden.getMesa().getNumero());
            orden.setEntregada(true);
            ordenesActivas.remove(orden);
        } else {
            System.out.println("El pedido aún no está completamente listo para entregar.");
            System.out.println("   Platillos pendientes: " + orden.getCantidadPlatillosPendientes() + 
                             "/" + orden.getTotalPlatillos());
        }
    }
    
    public int getMesasAtendidas() {
        return mesasAtendidas;
    }
    
    public boolean mostrarMenu() 
    {
	    System.out.println("\n=== MENÚ MESERO ===");
	    System.out.println("Bienvenido, " + this.getNombre());
	    System.out.println("1. Tomar pedido");
	    System.out.println("2. Ver mis órdenes");
	    System.out.println("3. Modificar orden");
	    System.out.println("4. Eliminar mi orden");
	    System.out.println("5. Entregar pedido");
	    System.out.println("6. Ver mesas disponibles");
	    System.out.println("7. Ver mis tareas");
	    System.out.println("8. Ver mi información");
	    System.out.println("9. Cerrar sesión");
	    System.out.print("Seleccione opción: ");
	    
	    int opcion = EntradaUtils.leerEntero(scanner);
	    
	    switch (opcion) {
	        case 1:
	            tomarPedido();
	            System.out.println("\nPresione Enter para continuar...");
	            scanner.nextLine();
	            return false;
	        case 2:
	            verOrdenesMesero();
	            System.out.println("\nPresione Enter para continuar...");
	            scanner.nextLine();
	            return false;
	        case 3:
	            modificarOrden();
	            System.out.println("\nPresione Enter para continuar...");
	            scanner.nextLine();
	            return false;
	        case 4:
	            eliminarOrdenMesero();
	            System.out.println("\nPresione Enter para continuar...");
	            scanner.nextLine();
	            return false;
	        case 5:
	            entregarPedido();
	            System.out.println("\nPresione Enter para continuar...");
	            scanner.nextLine();
	            return false;
	        case 6:
	            verMesasDisponibles();
	            System.out.println("\nPresione Enter para continuar...");
	            scanner.nextLine();
	            return false;
	        case 7:
	            mesero.consultarTareas();
	            System.out.println("\nPresione Enter para continuar...");
	            scanner.nextLine();
	            return false;
	        case 8:
	            mesero.mostrarInfo(false);
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

    private void eliminarOrdenMesero(Mesero mesero) {
        System.out.println("\n=== ELIMINAR MI ORDEN ===");
        
        // Mostrar solo órdenes de este mesero que no estén entregadas
        List<Orden> ordenesMesero = sistema.getOrdenes().stream()
            .filter(o -> o.getMesero().getId() == mesero.getId() && !o.isEntregada())
            .collect(java.util.stream.Collectors.toList());
        
        if (ordenesMesero.isEmpty()) {
            System.out.println("No tienes órdenes activas para eliminar.");
            return;
        }
        
        System.out.println("Tus órdenes activas:");
        for (Orden orden : ordenesMesero) {
            System.out.println("\n[Orden #" + orden.getId() + "]");
            System.out.println("Mesa: " + orden.getMesa().getNumero());
            System.out.println("Total: $" + orden.getTotal());
            System.out.println("Estado: " + (orden.estaLista() ? "LISTA" : "EN PREPARACIÓN"));
            System.out.println("-------------------");
        }
        
        System.out.print("\nID de tu orden a eliminar (0 para cancelar): ");
        int idOrden = EntradaUtils.leerEntero(scanner);
        
        if (idOrden == 0) {
            System.out.println("Operación cancelada.");
            return;
        }
        
        // Verificar que la orden pertenezca a este mesero
        Orden orden = sistema.buscarOrdenPorId(idOrden);
        
        if (orden == null || orden.getMesero().getId() != mesero.getId()) {
            System.out.println(" Esta orden no existe o no te pertenece.");
            return;
        }
        
        if (orden.isEntregada()) {
            System.out.println(" No puedes eliminar una orden ya entregada.");
            System.out.println("   Contacta a un administrador si hay un problema.");
            return;
        }
        
        // Verificar si hay platillos ya preparados
        if (orden.getCantidadPlatillosListos() > 0) {
            System.out.println(" Advertencia: Hay " + orden.getCantidadPlatillosListos() + 
                             " platillo(s) ya preparados.");
            System.out.print("¿Está seguro de continuar? (s/n): ");
            String respuesta = scanner.nextLine().toLowerCase();
            
            if (!respuesta.equals("s") && !respuesta.equals("si")) {
                System.out.println("Eliminación cancelada.");
                return;
            }
            
            System.out.println(" Informar al cocinero sobre los platillos preparados que se descartarán.");
        }
        
        System.out.print("Motivo breve (ej: cliente canceló, error en pedido): ");
        String motivo = scanner.nextLine();
        
        // Confirmación final
        System.out.print("\n¿CONFIRMAR eliminación de la orden #" + idOrden + "? (s/n): ");
        String confirmacion = scanner.nextLine().toLowerCase();
        
        if (!confirmacion.equals("s") && !confirmacion.equals("si")) {
            System.out.println("Eliminación cancelada.");
            return;
        }
        
        // Eliminar la orden
        boolean eliminada = sistema.eliminarOrden(idOrden);
        
        if (eliminada) {
            System.out.println(" Tu orden ha sido eliminada exitosamente.");
            
            // Registrar log
            GestorLogs.registrarLogEliminacionOrden(idOrden, "Mesero: " + motivo, this.getNombre());
        }
    }
    
    private void tomarPedido() 
    {
	    verMesasDisponibles();
	    System.out.print("Número de mesa: ");
	    int numeroMesa = EntradaUtils.leerEntero(scanner);
	    
	    Mesa mesa = sistema.getMesas().stream()
	        .filter(m -> m.getNumero() == numeroMesa && !m.isOcupada())
	        .findFirst()
	        .orElse(null);
	    
	    if (mesa == null) {
	        System.out.println("Mesa no disponible");
	        return;
	    }
	    
	    mesa.setOcupada(true);
	    
	    // Crear lista de ItemOrden
	    List<ItemOrden> items = new ArrayList<>();
	    boolean agregando = true;
	    
	    while (agregando) {
	        System.out.println("\n=== AGREGAR PLATILLOS A LA ORDEN ===");
	        System.out.println("Platillos disponibles:");
	        
	        for (Platillo platillo : sistema.getPlatillos()) {
	            System.out.println("[ID: " + platillo.getId() + "] " + 
	                             platillo.getNombre() + 
	                             " - $" + platillo.getPrecio() +
	                             " (" + platillo.getTiempoPreparacion() + " min)");
	        }
	        
	        System.out.print("\nID del platillo (0 para terminar): ");
	        int idPlatillo = EntradaUtils.leerEntero(scanner);
	        
	        if (idPlatillo == 0) {
	            agregando = false;
	        } else {
	            Platillo platillo = sistema.getPlatillos().stream()
	                .filter(p -> p.getId() == idPlatillo)
	                .findFirst()
	                .orElse(null);
	            
	            if (platillo != null) {
	                System.out.print("Cantidad de '" + platillo.getNombre() + "': ");
	                int cantidad = EntradaUtils.leerEntero(scanner);
	                
	                if (cantidad > 0) {
	                    items.add(new ItemOrden(platillo, cantidad));
	                    System.out.println(" Agregados " + cantidad + " x '" + platillo.getNombre() + "' a la orden");
	                } else {
	                    System.out.println(" La cantidad debe ser mayor a 0");
	                }
	            } else {
	                System.out.println(" Platillo no encontrado");
	            }
	        }
	    }
	    
	    // Verificar si se agregaron items
	    if (!items.isEmpty()) {
	    Orden orden = this.tomarPedido(mesa, items);
	    sistema.agregarOrden(orden); // Usar este método en lugar de add directo
	    System.out.println("\n Pedido registrado exitosamente");
	    System.out.println("Orden #" + orden.getId());
	    System.out.println("Total: $" + orden.getTotal());
	    sistema.agregarVenta(orden.getTotal());
	    
	    // Mostrar resumen de la orden
	    orden.mostrarOrden();
	}
	      else {
	        System.out.println(" No se agregaron platillos a la orden");
	        mesa.setOcupada(false); // Liberar la mesa
	    }
	}
    
    private void verOrdenesMesero() 
    {
	    List<Orden> ordenesMesero = sistema.getOrdenes().stream()
	        .filter(o -> o.getMesero().getId() == this.getId() && !o.isEntregada())
	        .collect(java.util.stream.Collectors.toList());
	    
	    if (ordenesMesero.isEmpty()) {
	        System.out.println("No tienes órdenes activas");
	    } else {
	        System.out.println("\n=== MIS ÓRDENES ACTIVAS ===");
	        System.out.println("Total órdenes: " + ordenesMesero.size());
	        
	        for (Orden orden : ordenesMesero) {
	            System.out.println("\n[Orden #" + orden.getId() + "]");
	            System.out.println("Mesa: " + orden.getMesa().getNumero());
	            
	            // Mostrar estado de la orden
	            if (orden.estaLista()) {
	                System.out.println("Estado: LISTA PARA ENTREGAR");
	            } else {
	                int total = orden.getTotalPlatillos();
	                int listos = orden.getCantidadPlatillosListos(); // Cambiado
	                System.out.println("Estado: EN PREPARACIÓN (" + listos + "/" + total + " platillos listos)");
	            }
	            
	            // Mostrar items con su estado
	            System.out.println("Items:");
	            for (ItemOrden item : orden.getItems()) {
	                System.out.println("  - " + item.getPlatillo().getNombre() + 
	                                 " x" + item.getCantidad() + 
	                                 " | $" + item.getPlatillo().getPrecio() + " c/u" +
	                                 " | Estado: " + item.getCantidadLista() + "/" + item.getCantidad() + " listos");
	            }
	            
	            System.out.println("Total: $" + orden.getTotal());
	            System.out.println("-------------------");
	        }
	    }
	}
    
    private void entregarPedido() 
    {
	    verOrdenesMesero();
	    System.out.print("ID de la orden a entregar: ");
	    int idOrden = EntradaUtils.leerEntero(scanner);
	    
	    Orden orden = sistema.getOrdenes().stream()
	        .filter(o -> o.getId() == idOrden && o.getMesero().getId() == mesero.getId())
	        .findFirst()
	        .orElse(null);
	    
	    if (orden != null) {
	        if (!orden.estaLista()) {
	            System.out.println("  Esta orden no está completamente lista.");
	            System.out.println("Platillos pendientes: " + 
	                             orden.getCantidadPlatillosPendientes() + "/" + 
	                             orden.getTotalPlatillos());
	            System.out.print("¿Desea continuar con la entrega? (s/n): ");
	            String respuesta = scanner.nextLine().toLowerCase();
	            if (!respuesta.equals("s") && !respuesta.equals("si")) {
	                System.out.println("Entrega cancelada.");
	                return;
	            }
	        }
	        
	        // Llama al método entregarPedido del mesero
	        this.entregarPedido(orden);
	        
	        // Liberar la mesa si la orden se entregó
	        if (orden.isEntregada()) {
	            orden.getMesa().setOcupada(false);
	            System.out.println(" Mesa " + orden.getMesa().getNumero() + " liberada.");
	        }
	    } else {
	        System.out.println("Orden no encontrada o no pertenece a este mesero");
	    }
	    
	    // Llama al método entregarPedido del mesero
	    this.entregarPedido(orden);
	
	    // Liberar la mesa si la orden se entregó
		if (orden.isEntregada()) {
		    orden.getMesa().setOcupada(false);
		    System.out.println(" Mesa " + orden.getMesa().getNumero() + " liberada.");
		    // Guardar estado
		    sistema.guardarEstado();
		}
	}

}
