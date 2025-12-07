package restaurante;

public class ItemOrden {
    private Platillo platillo;
    private int cantidad;
    private int cantidadLista;
    
    public ItemOrden(Platillo platillo, int cantidad) {
        this.platillo = platillo;
        this.cantidad = cantidad;
        this.cantidadLista = 0;
    }
    
    public void marcarListo() {
        if (cantidadLista < cantidad) {
            cantidadLista++;
        }
    }
    
    public void marcarListo(int cantidadAMarcar) {
        int disponible = cantidad - cantidadLista;
        int aMarcar = Math.min(cantidadAMarcar, disponible);
        cantidadLista += aMarcar;
    }
    
    public boolean estaCompleto() {
        return cantidadLista >= cantidad;
    }
    
    // Getters
    public Platillo getPlatillo() { 
        return platillo; 
    }
    
    public int getCantidad() { 
        return cantidad; 
    }
    
    public int getCantidadLista() { 
        return cantidadLista; 
    }
    
    public int getCantidadPendiente() { 
        return cantidad - cantidadLista; 
    }
    
    public double getSubtotal() { 
        return platillo.getPrecio() * cantidad; 
    }
    
    public void mostrarItem() {
        System.out.println("  [ID: " + platillo.getId() + "] " + 
                         platillo.getNombre() + 
                         " x" + cantidad + 
                         " - $" + platillo.getPrecio() + " c/u" +
                         " | Subtotal: $" + getSubtotal() +
                         " | Estado: " + cantidadLista + "/" + cantidad + " listos");
    }
    
    // Setters
    public void setCantidad(int cantidad) {
        if (cantidad >= 0) {
            // Ajustar cantidadLista si la nueva cantidad es menor
            if (cantidad < this.cantidadLista) {
                this.cantidadLista = cantidad;
            }
            this.cantidad = cantidad;
        }
    }
    
    
    public void setCantidadLista(int cantidadLista) {
        if (cantidadLista >= 0 && cantidadLista <= cantidad) {
            this.cantidadLista = cantidadLista;
        }
    }
}
