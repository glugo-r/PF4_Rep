package servicios;

import principal.SistemaTareas;
import tareas.Tarea;
import utilidades.EntradaUtils;
import java.util.Scanner;

public class GestorTareas 
{
	private SistemaTareas sistema;
    private Scanner scanner;

    public GestorTareas(SistemaTareas sistema, Scanner scanner) 
    {
        this.sistema = sistema;
        this.scanner = scanner;
    }

	public void eliminarTarea() 
	{
	    System.out.println("\n=== ELIMINAR TAREA ===");
	    
	    if (sistema.getTareas().isEmpty()) {
	        System.out.println("No hay tareas registradas.");
	        return;
	    }
	    
	    System.out.println("Lista de tareas:");
	    sistema.listarTareas();
	    
	    System.out.print("\nID de la tarea a eliminar (0 para cancelar): ");
	    int idTarea = EntradaUtils.leerEntero(scanner);
	    
	    if (idTarea == 0) {
	        System.out.println("Operación cancelada.");
	        return;
	    }
	    
	    Tarea tarea = sistema.buscarTareaPorId(idTarea);
	    if (tarea == null) {
	        System.out.println(" Tarea no encontrada.");
	        return;
	    }
	    
	    System.out.println("Tarea a eliminar:");
	    tarea.mostrarDetalles();
	    
	    System.out.print("\n¿Está seguro de eliminar esta tarea? (s/n): ");
	    String confirmacion = scanner.nextLine().toLowerCase();
	    
	    if (confirmacion.equals("s") || confirmacion.equals("si")) {
	        // Remover tarea de cualquier empleado asignado
	        if (tarea.getUsuarioAsignado() != null) {
	            tarea.getUsuarioAsignado().removerTarea(tarea);
	        }
	        
	        // Eliminar tarea del sistema
	        sistema.getTareas().removeIf(t -> t.getId() == idTarea);
	        System.out.println(" Tarea eliminada exitosamente.");
	    } else {
	        System.out.println("Operación cancelada.");
	    }
	}

}
