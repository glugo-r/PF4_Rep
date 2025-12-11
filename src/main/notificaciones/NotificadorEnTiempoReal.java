package notificaciones;

import usuarios.Empleado;

public class NotificadorEnTiempoReal implements Runnable {
    private Empleado empleado;
    private volatile boolean activo = true;

    public NotificadorEnTiempoReal(Empleado empleado) {
        this.empleado = empleado;
    }

    @Override
    public void run() {
        while (activo) {
            try {
                Thread.sleep(15000); // pausa de 15 segundos
            } catch (InterruptedException e) {
                // Si el hilo fue interrumpido, salimos del bucle inmediatamente
                Thread.currentThread().interrupt();
                break;
            }

            // Chequeo extra: si ya se pidió detener, no imprimir nada
            if (!activo) break;

            if (!empleado.getNotificaciones().isEmpty()) {
                System.out.println("\n=== NUEVAS NOTIFICACIONES ===");
                for (String n : empleado.getNotificaciones()) {
                    System.out.println(" " + n);
                }
            }
        }
    }

    public void detener() {
        activo = false;
    }
}