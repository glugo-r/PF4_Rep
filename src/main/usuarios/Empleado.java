package usuarios;

import java.util.ArrayList;
import java.util.List;

import tareas.EstadoTarea;
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
        notificaciones.clear(); // limpiar después de mostrar
    }

    public void completarTarea(Tarea tarea) {
        if (tareasAsignadas.contains(tarea)) {
            tarea.cambiarEstado(EstadoTarea.FINALIZADA);
            System.out.println("La tarea '" + tarea.getTitulo() + "' ha sido completada.");
        } else {
            System.out.println("La tarea no está asignada a este empleado.");
        }
    }

    // Método para mostrar tareas pendientes
    public void mostrarTareasPendientes() {
        System.out.println("=== TAREAS PENDIENTES ===");
        for (Tarea t : tareasAsignadas) {
            if (t.getEstado() != EstadoTarea.FINALIZADA) {
                System.out.println("ID: " + t.getId() + " | " + t.getTitulo() + " (Estado: " + t.getEstado() + ")");
            }
        }
    }

    public Tarea buscarTareaPorId(int id) {
        for (Tarea t : tareasAsignadas) {
            if (t.getId() == id) {   //  comparación por ID único
                return t;
            }
        }
        return null;
    }

    public void completarTareaPorId(int id) {
        Tarea tarea = buscarTareaPorId(id);
        if (tarea != null) {
            tarea.cambiarEstado(EstadoTarea.FINALIZADA);
            System.out.println("La tarea '" + tarea.getTitulo() + "' (ID: " + tarea.getId() + ") ha sido completada.");
        } else {
            System.out.println("No se encontró la tarea con ese ID.");
        }
    }

}
