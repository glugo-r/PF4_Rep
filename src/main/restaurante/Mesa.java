package restaurante;

public class Mesa {
    private int numero;
    private int capacidad;
    private boolean ocupada;
    
    public Mesa(int numero, int capacidad) {
        this.numero = numero;
        this.capacidad = capacidad;
        this.ocupada = false;
    }
    
    // Getters y Setters
    public int getNumero() { return numero; }
    public int getCapacidad() { return capacidad; }
    public boolean isOcupada() { return ocupada; }
    public void setOcupada(boolean ocupada) { this.ocupada = ocupada; }
}
