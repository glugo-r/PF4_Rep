package restaurante;

public class Platillo {
    private static int contadorId = 1;
    private int id;
    private String nombre;
    private String descripcion;
    private double precio;
    private int tiempoPreparacion; // en minutos
    private String categoria;
    
    public Platillo(String nombre, String descripcion, double precio, 
                   int tiempoPreparacion, String categoria) {
        this.id = contadorId++;
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.precio = precio;
        this.tiempoPreparacion = tiempoPreparacion;
        this.categoria = categoria;
    }
    
    // Getters
    public int getId() { return id; }
    public String getNombre() { return nombre; }
    public String getDescripcion() { return descripcion; }
    public double getPrecio() { return precio; }
    public int getTiempoPreparacion() { return tiempoPreparacion; }
    public String getCategoria() { return categoria; }
    
    public void mostrarInfo() {
        System.out.println("ID: " + id + " | " + nombre + " - $" + precio);
        System.out.println("Descripción: " + descripcion);
        System.out.println("Tiempo preparación: " + tiempoPreparacion + " min");
    }
}
