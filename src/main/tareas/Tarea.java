package tareas;

import usuarios.Empleado;

public class Tarea {
    private static int contadorId = 1;
    private int id;
    private String titulo;
    private String descripcion;
    private String fechaLimite;
    private EstadoTarea estado;
    private Empleado usuarioAsignado;
    
    // Constructor normal (para nuevas tareas)
    public Tarea(String titulo, String descripcion, String fechaLimite) {
        this.id = contadorId++;
        this.titulo = titulo;
        this.descripcion = descripcion;
        this.fechaLimite = fechaLimite;
        this.estado = EstadoTarea.PENDIENTE;
    }
    
    // Constructor adicional (para reconstrucción desde CSV)
    public Tarea(int id, String titulo, String descripcion, String fechaLimite, EstadoTarea estado) {
        this.id = id;
        this.titulo = titulo;
        this.descripcion = descripcion;
        this.fechaLimite = fechaLimite;
        this.estado = estado;
    }
    
    // Métodos para manejar el contador
    public static int getContadorId() {
        return contadorId;
    }

    public static void setContadorId(int nuevoValor) {
        contadorId = nuevoValor;
    }
    
    public void cambiarEstado(EstadoTarea nuevoEstado) {
        this.estado = nuevoEstado;
    }
    
    public void mostrarDetalles() {
        System.out.println("ID Tarea: " + id);
        System.out.println("Título: " + titulo);
        System.out.println("Descripción: " + descripcion);
        System.out.println("Fecha Límite: " + fechaLimite);
        System.out.println("Estado: " + estado);
        if (usuarioAsignado != null) {
            System.out.println("Asignado a: " + usuarioAsignado.getNombre());
        }
    }
    
    // Getters y Setters
    public int getId() { return id; }
    public String getTitulo() { return titulo; }
    public String getDescripcion() { return descripcion; }
    public String getFechaLimite() { return fechaLimite; }
    public EstadoTarea getEstado() { return estado; }
    public Empleado getUsuarioAsignado() { return usuarioAsignado; }
    
    public void setUsuarioAsignado(Empleado usuarioAsignado) { 
        this.usuarioAsignado = usuarioAsignado; 
    }
    public void setFechaLimite(String fechaLimite) { 
        this.fechaLimite = fechaLimite; 
    }
}
