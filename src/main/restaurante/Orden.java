package restaurante;

import usuarios.Mesero;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Iterator;
import java.text.DecimalFormat;

public class Orden {
    private static int contadorId = 1;
    private int id;
    private Mesa mesa;
    private Mesero mesero;
    private Date fecha;
    private List<ItemOrden> items;
    private boolean entregada;
    private double total;
    
    public Orden(Mesa mesa, Mesero mesero) {
        this.id = contadorId++;
        this.mesa = mesa;
        this.mesero = mesero;
        this.fecha = new Date();
        this.items = new ArrayList<>();
        this.entregada = false;
        this.total = 0.0;
    }
    
    public void agregarPlatillo(Platillo platillo, int cantidad) {
        // Verificar si ya existe este platillo en la orden
        for (ItemOrden item : items) {
            if (item.getPlatillo().getId() == platillo.getId()) {
                // agregamos como nuevo item
            }
        }
        items.add(new ItemOrden(platillo, cantidad));
        total += platillo.getPrecio() * cantidad;
    }
    
    public void agregarItem(ItemOrden item) {
        items.add(item);
        total += item.getSubtotal();
    }
    
    public void marcarPlatilloListo(Platillo platillo) {
	    for (ItemOrden item : items) {
	        if (item.getPlatillo().getId() == platillo.getId() && !item.estaCompleto()) {
	            item.marcarListo(); // Esto solo marca una unidad
	            return;
	        }
	    }
    }
    
    public String getTotalFormateado() {
        DecimalFormat formato = new DecimalFormat("$#,##0.00");
        return formato.format(total);
    }
    
	// Método para eliminar un platillo de la orden
	public boolean eliminarPlatillo(int platilloId) {
	    Iterator<ItemOrden> iterator = items.iterator();
	    while (iterator.hasNext()) {
	        ItemOrden item = iterator.next();
	        if (item.getPlatillo().getId() == platilloId) {
	            // Ajustar el total
	            total -= item.getSubtotal();
	            iterator.remove();
	            return true;
	        }
	    }
	    return false;
	}

	// Método para modificar la cantidad de un platillo
	public boolean modificarCantidadPlatillo(int platilloId, int nuevaCantidad) {
	    if (nuevaCantidad <= 0) {
	        // Si la nueva cantidad es 0 o negativa, eliminar el platillo
	        return eliminarPlatillo(platilloId);
	    }
	    
	    for (ItemOrden item : items) {
	        if (item.getPlatillo().getId() == platilloId) {
	            // Ajustar el total primero
	            total -= item.getSubtotal();
	            
	            // Guardar la cantidad lista actual
	            int cantidadListaActual = item.getCantidadLista();
	            
	            //Actualizar la cantidad
	            item.setCantidad(nuevaCantidad);
	            
	            // Si la nueva cantidad es menor que la cantidad ya lista,
	            // ajustar la cantidad lista
	            if (nuevaCantidad < item.getCantidadLista()) {
	                item.setCantidadLista(nuevaCantidad);
	            }
	            
	            total+=item.getSubtotal();
	            return true;
	        }
	    }
	    return false;
	}
	
	// Método para agregar más cantidad a un platillo existente
	public boolean agregarCantidadPlatillo(int platilloId, int cantidadAdicional) {
	    for (ItemOrden item : items) {
	        if (item.getPlatillo().getId() == platilloId) {
	            // Necesitamos un método en ItemOrden para agregar cantidad
	            // Por ahora, usaremos modificarCantidadPlatillo
	            int nuevaCantidad = item.getCantidad() + cantidadAdicional;
	            return modificarCantidadPlatillo(platilloId, nuevaCantidad);
	        }
	    }
	    return false;
	}
	
	// Método para agregar un nuevo platillo a la orden
	public void agregarNuevoPlatillo(Platillo platillo, int cantidad) {
	    // Verificar si ya existe
	    for (ItemOrden item : items) {
	        if (item.getPlatillo().getId() == platillo.getId()) {
	            // Si ya existe, agregar a la cantidad existente
	            agregarCantidadPlatillo(platillo.getId(), cantidad);
	            return;
	        }
	    }
	    
	    // Si no existe, agregar nuevo item
	    ItemOrden nuevoItem = new ItemOrden(platillo, cantidad);
	    items.add(nuevoItem);
	    total += nuevoItem.getSubtotal();
	}

    public ItemOrden encontrarItemPorPlatilloId(int platilloId) {
        for (ItemOrden item : items) {
            if (item.getPlatillo().getId() == platilloId) {
                return item;
            }
        }
        return null;
    }
    
    
    public boolean estaLista() {
        if (items.isEmpty()) return false;
        for (ItemOrden item : items) {
            if (!item.estaCompleto()) {
                return false;
            }
        }
        return true;
    }
    
    public boolean platilloEstaEnOrden(Platillo platillo) {
        for (ItemOrden item : items) {
            if (item.getPlatillo().getId() == platillo.getId() && !item.estaCompleto()) {
                return true;
            }
        }
        return false;
    }
    
    public List<ItemOrden> getItemsPendientes() {
        List<ItemOrden> pendientes = new ArrayList<>();
        for (ItemOrden item : items) {
            if (!item.estaCompleto()) {
                pendientes.add(item);
            }
        }
        return pendientes;
    }
    
    public List<ItemOrden> getItemsListos() {
        List<ItemOrden> listos = new ArrayList<>();
        for (ItemOrden item : items) {
            if (item.estaCompleto()) {
                listos.add(item);
            }
        }
        return listos;
    }
    
    public int getTotalPlatillos() {
        int total = 0;
        for (ItemOrden item : items) {
            total += item.getCantidad();
        }
        return total;
    }
    
    public int getCantidadPlatillosListos() {
        int listos = 0;
        for (ItemOrden item : items) {
            listos += item.getCantidadLista();
        }
        return listos;
    }
    
    public int getCantidadPlatillosPendientes() {
        return getTotalPlatillos() - getCantidadPlatillosListos();
    }
    
    // Para compatibilidad con código que espera lista de platillos
    public List<Platillo> getPlatillos() {
        List<Platillo> platillos = new ArrayList<>();
        for (ItemOrden item : items) {
            for (int i = 0; i < item.getCantidad(); i++) {
                platillos.add(item.getPlatillo());
            }
        }
        return platillos;
    }
    
    // Para compatibilidad
    public List<Platillo> getListaPlatillosListos() {
        List<Platillo> platillosListos = new ArrayList<>();
        for (ItemOrden item : items) {
            for (int i = 0; i < item.getCantidadLista(); i++) {
                platillosListos.add(item.getPlatillo());
            }
        }
        return platillosListos;
    }
    
    public void mostrarOrden() {
        System.out.println("\n=== ORDEN #" + id + " ===");
        System.out.println("Mesa: " + mesa.getNumero());
        System.out.println("Mesero: " + mesero.getNombre());
        System.out.println("Fecha: " + fecha);
        System.out.println("Items:");
        
        for (ItemOrden item : items) {
            item.mostrarItem();
        }
        
        System.out.println("Total: $" + total);
        System.out.println("Estado general: " + (estaLista() ? "LISTA PARA ENTREGAR" : " EN PREPARACIÓN"));
        System.out.println("Progreso: " + getCantidadPlatillosListos() + "/" + getTotalPlatillos() + " platillos listos");
    }
    
    public void mostrarParaCocinero() {
        System.out.println("\n=== ORDEN #" + id + " ===");
        System.out.println("Mesa: " + mesa.getNumero());
        System.out.println("Items a preparar:");
        
        if (items.isEmpty()) {
            System.out.println("  No hay items en esta orden.");
            return;
        }
        
        for (ItemOrden item : items) {
            if (!item.estaCompleto()) {
                System.out.println("  [ID Platillo: " + item.getPlatillo().getId() + "] " + 
                                 item.getPlatillo().getNombre() + 
                                 " x" + item.getCantidadPendiente() + 
                                 " de " + item.getCantidad() + 
                                 " | Tiempo: " + item.getPlatillo().getTiempoPreparacion() + " min c/u");
            }
        }
        
        // Mostrar items ya listos
        boolean hayListos = false;
        for (ItemOrden item : items) {
            if (item.getCantidadLista() > 0) {
                if (!hayListos) {
                    System.out.println("\nItems ya listos:");
                    hayListos = true;
                }
                System.out.println("  [ID Platillo: " + item.getPlatillo().getId() + "] " + 
                                 item.getPlatillo().getNombre() + 
                                 " " + item.getCantidadLista() + "/" + item.getCantidad() + " ");
            }
        }
    }
    
    // Getters y Setters
    public int getId() { return id; }
    public Mesa getMesa() { return mesa; }
    public Mesero getMesero() { return mesero; }
    public Date getFecha() { return fecha; }
    public double getTotal() { return total; }
    public boolean isEntregada() { return entregada; }
    public List<ItemOrden> getItems() { return new ArrayList<>(items); }
    public void setEntregada(boolean entregada) { this.entregada = entregada; }
}
