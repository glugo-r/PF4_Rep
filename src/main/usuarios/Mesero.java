package usuarios;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import principal.SistemaTareas;
import restaurante.*;
import utilidades.EntradaUtils;

public class Mesero extends Empleado 
{
	private SistemaTareas sistema;
    private Scanner scanner;
	
    private int mesasAtendidas;
    private List<Orden> ordenesActivas;
    
    public Mesero(String nombre, String email, String password) {
        super(nombre, email, "Mesero", password);
        this.mesasAtendidas = 0;
        this.ordenesActivas = new ArrayList<>();
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
            System.out.println("✅ Pedido entregado a la mesa " + orden.getMesa().getNumero());
            orden.setEntregada(true);
            ordenesActivas.remove(orden);
        } else {
            System.out.println("⚠️  El pedido aún no está completamente listo para entregar.");
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
	    System.out.println("Bienvenido, " + mesero.getNombre());
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
	            tomarPedido(mesero);
	            System.out.println("\nPresione Enter para continuar...");
	            scanner.nextLine();
	            return false;
	        case 2:
	            verOrdenesMesero(mesero);
	            System.out.println("\nPresione Enter para continuar...");
	            scanner.nextLine();
	            return false;
	        case 3:
	            modificarOrden(mesero);
	            System.out.println("\nPresione Enter para continuar...");
	            scanner.nextLine();
	            return false;
	        case 4:
	            eliminarOrdenMesero(mesero);
	            System.out.println("\nPresione Enter para continuar...");
	            scanner.nextLine();
	            return false;
	        case 5:
	            entregarPedido(mesero);
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

}
