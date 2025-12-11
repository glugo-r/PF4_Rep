package usuarios;

import java.util.ArrayList;
import java.util.List;
import tareas.Tarea;

public abstract class Empleado extends Usuario {
    protected List<Tarea> tareasAsignadas;
    
    public Empleado(String nombre, String email, String rol, String password) {
        super(nombre, email, rol, password);
        this.tareasAsignadas = new ArrayList<>();
    }
    
    public void consultarTareas() {
        if (tareasAsignadas.isEmpty()) {
            System.out.println("No hay tareas asignadas.");
            return;
        }
        for (Tarea tarea : tareasAsignadas) {
            tarea.mostrarDetalles();
        }
    }
    
    public void agregarTarea(Tarea tarea) {
        tareasAsignadas.add(tarea);
    }
    
    public void removerTarea(Tarea tarea) {
        tareasAsignadas.remove(tarea);
    }
    
    public List<Tarea> getTareasAsignadas() {
        return tareasAsignadas;
    }
    
    
    // Métodos de notificaciones
    private List<String> notificaciones = new ArrayList<>();

    public void agregarNotificacion(String mensaje) 
    {
        notificaciones.add(mensaje);
    }

    public List<String> getNotificaciones() 
    {
        return notificaciones;
    }

    public void eliminarNotificacionesDeTarea(String tituloTarea) 
    {
        notificaciones.removeIf(n -> n.contains("'" + tituloTarea + "'"));
    }

    public void mostrarNotificacionesPendientes() 
    {
        if (!notificaciones.isEmpty()) 
        {
            System.out.println("\n=== NOTIFICACIONES ===");
            for (String n : notificaciones)
                System.out.println(" " + n);
        }
    }

    
}
