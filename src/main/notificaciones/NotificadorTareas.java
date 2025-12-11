package notificaciones;

import tareas.Tarea;
import tareas.EstadoTarea;
import java.util.List;
import java.text.SimpleDateFormat;
import java.util.Date;

public class NotificadorTareas extends Thread {
    private List<Tarea> tareas;
    private boolean activo;
    private static final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd H:mm");
    
    public NotificadorTareas(List<Tarea> tareas) {
        this.tareas = tareas;
        this.activo = true;
    }
    
    @Override
    public void run() {
        while (activo) {
            try {
                verificarTareasPorVencer();
                Thread.sleep(60000); // Revisar cada minuto
            } catch (InterruptedException e) {
                System.out.println("Notificador interrumpido");
            }
        }
    }
    
    private void verificarTareasPorVencer() {
        Date ahora = new Date();
        
        for (Tarea tarea : tareas) {
            if (tarea.getEstado() != EstadoTarea.FINALIZADA) {
                try {
                    Date fechaLimite = sdf.parse(tarea.getFechaLimite());
                    long diferencia = fechaLimite.getTime() - ahora.getTime();
                    long horasRestantes = diferencia / (60 * 60 * 1000);
                    
                    if (horasRestantes <= 2 && horasRestantes > 0) {
                        tarea.getUsuarioAsignado().agregarNotificacion(
                            "La tarea '" + tarea.getTitulo() + "' vence en " + horasRestantes + " horas"
                        );
                    } else if (diferencia < 0) {
                        tarea.getUsuarioAsignado().agregarNotificacion(
                            "La tarea '" + tarea.getTitulo() + "' está VENCIDA!"
                        );
                        tarea.cambiarEstado(EstadoTarea.VENCIDA);
                    }
                } catch (Exception e) {
                    // Formato de fecha inválido, continuar con siguiente tarea
                }
            }
        }
    }
    
    public void detener() {
        this.activo = false;
    }
}
