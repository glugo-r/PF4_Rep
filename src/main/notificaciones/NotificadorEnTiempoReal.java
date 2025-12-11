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
                Thread.sleep(5000); // cada 5 segundos
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }

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