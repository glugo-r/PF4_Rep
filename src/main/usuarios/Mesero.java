package usuarios;

import java.util.ArrayList;
import java.util.List;
import restaurante.*;

public class Mesero extends Empleado {
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
}
