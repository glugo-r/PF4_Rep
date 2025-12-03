package usuarios;

import restaurante.Platillo;
import restaurante.Orden;
import database.DatabaseManager;

public class Cocinero extends Empleado {
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
}
