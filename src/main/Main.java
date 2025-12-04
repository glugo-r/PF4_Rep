import usuarios.*;
import tareas.*;
import restaurante.*;
import excepciones.*;
import database.DatabaseManager;
import notificaciones.NotificadorTareas;
import java.util.*;
import java.text.SimpleDateFormat;

public class Main {
    private static SistemaTareas sistema;
    private static Scanner scanner;
    private static NotificadorTareas notificador;
    private static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
    
    public static void main(String[] args) {
        sistema = new SistemaTareas();
        scanner = new Scanner(System.in);
        
        // Iniciar hilo de notificaciones
        notificador = new NotificadorTareas(sistema.getTareas());
        notificador.start();
        
        // Agregar shutdown hook para guardar automáticamente
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.out.println("\n💾 Guardando estado del sistema...");
            sistema.guardarEstado();
            notificador.detener();
            System.out.println("✅ Sistema cerrado correctamente.");
        }));
        
        System.out.println("=== SISTEMA DE GESTIÓN DE RESTAURANTE ===");
        mostrarMenuPrincipal();
        
        sistema.guardarEstado();
        scanner.close();
        notificador.detener();
    }
    
    private static void mostrarMenuPrincipal() {
        boolean salir = false;
        
        while (!salir) {
            System.out.println("\n=== MENÚ PRINCIPAL ===");
            System.out.println("1. Iniciar sesión");
            System.out.println("2. Salir");
            System.out.print("Seleccione opción: ");
            
            int opcion = leerEntero();
            
            switch (opcion) {
                case 1:
                    iniciarSesion();
                    break;
                case 2:
                    salir = true;
                    System.out.println("Saliendo del sistema...");
                    break;
                default:
                    System.out.println("Opción inválida");
            }
        }
    }
    
    private static void iniciarSesion() {
        System.out.print("Ingrese email: ");
        String email = scanner.nextLine();
        
        System.out.print("Ingrese contraseña: ");
        String password = scanner.nextLine();
        
        // Autenticar usuario
        Usuario usuario = sistema.autenticarUsuario(email, password);
        
        if (usuario != null) {
            sistema.setUsuarioActual(usuario);
            System.out.println("¡Bienvenido, " + usuario.getNombre() + "!");
            mostrarMenuSegunRol();
        } else {
            System.out.println("Credenciales incorrectas. Intente nuevamente.");
        }
    }
    
    private static void mostrarMenuSegunRol() {
        Usuario usuario = sistema.getUsuarioActual();
        boolean cerrarSesion = false;
        
        while (!cerrarSesion) {
            System.out.println("\n=== MENÚ DE " + usuario.getRol().toUpperCase() + " ===");
            
            if (usuario instanceof Sudo) {
                cerrarSesion = mostrarMenuSudo();
            } else if (usuario instanceof Administrador) {
                cerrarSesion = mostrarMenuAdministrador((Administrador) usuario);
            } else if (usuario instanceof Cocinero) {
                cerrarSesion = mostrarMenuCocinero((Cocinero) usuario);
            } else if (usuario instanceof Mesero) {
                cerrarSesion = mostrarMenuMesero((Mesero) usuario);
            } else {
                System.out.println("Rol no reconocido");
                cerrarSesion = true;
            }
        }
    }
    
    private static boolean mostrarMenuSudo() {
    System.out.println("\n=== MENÚ SUDO ===");
    System.out.println("1. Crear nuevo usuario");
    System.out.println("2. Listar todos los usuarios");
    System.out.println("3. Ver ventas del día");
    System.out.println("4. Ver/eliminar tareas");
    System.out.println("5. Ver mi información");
    System.out.println("6. Cambiar mi contraseña");
    System.out.println("7. Eliminar usuario");
    System.out.println("8. Eliminar orden"); 
    System.out.println("9. Cerrar sesión");
    System.out.print("Seleccione opción: ");
    
    int opcion = leerEntero();
    
    switch (opcion) {
        case 1:
            crearNuevoUsuario();
            System.out.println("\nPresione Enter para continuar...");
            scanner.nextLine();
            return false;
        case 2:
            System.out.println("\n¿Mostrar contraseñas? (s/n): ");
            String respuesta = scanner.nextLine().toLowerCase();
            boolean mostrarPasswords = respuesta.equals("s") || respuesta.equals("si");
            sistema.listarUsuarios(mostrarPasswords);
            System.out.println("\nPresione Enter para continuar...");
            scanner.nextLine();
            return false;
        case 3:
            System.out.println("Ventas del día: $" + sistema.getVentasDia());
            System.out.println("\nPresione Enter para continuar...");
            scanner.nextLine();
            return false;
        case 4:
            gestionarTareasSudo();
            System.out.println("\nPresione Enter para continuar...");
            scanner.nextLine();
            return false;
        case 5:
            sistema.getUsuarioActual().mostrarInfo(true);
            System.out.println("\nPresione Enter para continuar...");
            scanner.nextLine();
            return false;
        case 6:
            cambiarPasswordSudo();
            System.out.println("\nPresione Enter para continuar...");
            scanner.nextLine();
            return false;
        case 7:
            eliminarUsuarioSudo();
            System.out.println("\nPresione Enter para continuar...");
            scanner.nextLine();
            return false;
        case 8:
            eliminarOrdenSudo();
            System.out.println("\nPresione Enter para continuar...");
            scanner.nextLine();
            return false;
        case 9:
            sistema.setUsuarioActual(null);
            System.out.println("Sesión cerrada correctamente.");
            return true;
        default:
            System.out.println("Opción inválida");
            System.out.println("\nPresione Enter para continuar...");
            scanner.nextLine();
            return false;
    }
}

private static void gestionarTareasSudo() {
    System.out.println("\n=== GESTIÓN DE TAREAS ===");
    System.out.println("1. Listar todas las tareas");
    System.out.println("2. Eliminar tarea");
    System.out.print("Seleccione opción: ");
    
    int opcion = leerEntero();
    
    switch (opcion) {
        case 1:
            sistema.listarTareas();
            break;
        case 2:
            eliminarTarea();
            break;
        default:
            System.out.println("Opción inválida");
    }
}
    
    private static void cambiarPasswordSudo() {
        System.out.print("Ingrese nueva contraseña: ");
        String nuevaPassword = scanner.nextLine();
        
        System.out.print("Confirme nueva contraseña: ");
        String confirmPassword = scanner.nextLine();
        
        if (nuevaPassword.equals(confirmPassword)) {
            if (nuevaPassword.length() < 4) {
                System.out.println("La contraseña debe tener al menos 4 caracteres.");
                return;
            }
            
            sistema.getUsuarioActual().setPassword(nuevaPassword);
            DatabaseManager.guardarUsuarios(sistema.getUsuarios());
            System.out.println("Contraseña cambiada exitosamente.");
        } else {
            System.out.println("Las contraseñas no coinciden.");
        }
    }
    
    private static void eliminarUsuarioSudo() {
    System.out.println("\n=== ELIMINAR USUARIO ===");
    sistema.listarUsuarios(false);
    
    System.out.print("\nID del usuario a eliminar (0 para cancelar): ");
    int idUsuario = leerEntero();
    
    if (idUsuario == 0) {
        System.out.println("Operación cancelada.");
        return;
    }
    
    if (idUsuario == 1) {
        System.out.println("No se puede eliminar al usuario Sudo (ID: 1).");
        return;
    }
    
    Usuario usuario = sistema.buscarUsuarioPorId(idUsuario);
    if (usuario == null) {
        System.out.println("Usuario no encontrado.");
        return;
    }
    
    System.out.println("Usuario a eliminar:");
    usuario.mostrarInfo(false);
    
    System.out.print("\n¿Está seguro de eliminar este usuario? (s/n): ");
    String confirmacion = scanner.nextLine().toLowerCase();
    
    if (confirmacion.equals("s") || confirmacion.equals("si")) {
        sistema.eliminarUsuario(idUsuario);
        System.out.println("Usuario eliminado exitosamente.");
    } else {
        System.out.println("Operación cancelada.");
    }
}
    
    private static void eliminarOrdenSudo() {
        System.out.println("\n=== ELIMINAR ORDEN ===");
        
        // Mostrar todas las órdenes
        List<Orden> todasLasOrdenes = sistema.getOrdenes();
        
        if (todasLasOrdenes.isEmpty()) {
            System.out.println("No hay órdenes registradas en el sistema.");
            return;
        }
        
        System.out.println("Lista de todas las órdenes:");
        System.out.println("===========================");
        
        for (Orden orden : todasLasOrdenes) {
            System.out.println("\n[Orden #" + orden.getId() + "]");
            System.out.println("Mesa: " + orden.getMesa().getNumero());
            System.out.println("Mesero: " + orden.getMesero().getNombre());
            System.out.println("Fecha: " + orden.getFecha());
            System.out.println("Total: $" + orden.getTotal());
            System.out.println("Estado: " + (orden.isEntregada() ? "✅ ENTREGADA" : "🔄 ACTIVA"));
            System.out.println("Lista: " + (orden.estaLista() ? "✅ SÍ" : "❌ NO"));
            System.out.println("-------------------");
        }
        
        System.out.print("\nID de la orden a eliminar (0 para cancelar): ");
        int idOrden = leerEntero();
        
        if (idOrden == 0) {
            System.out.println("Operación cancelada.");
            return;
        }
        
        // Buscar la orden
        Orden orden = sistema.buscarOrdenPorId(idOrden);
        
        if (orden == null) {
            System.out.println("Orden no encontrada.");
            return;
        }
        
        // Mostrar detalles completos de la orden
        System.out.println("\n=== DETALLES DE LA ORDEN A ELIMINAR ===");
        orden.mostrarOrden();
        
        // Preguntar motivo
        System.out.println("\nADVERTENCIA: Esta acción no se puede deshacer");
        System.out.println("Motivos comunes para eliminar órdenes:");
        System.out.println("1. Error al tomar la orden");
        System.out.println("2. Cliente canceló el pedido");
        System.out.println("3. Problema con el pago");
        System.out.println("4. Otra razón");
        
        System.out.print("\nSeleccione motivo (1-4): ");
        int motivo = leerEntero();
        
        String[] motivos = {
            "Error al tomar la orden",
            "Cliente canceló el pedido", 
            "Problema con el pago",
            "Otra razón"
        };
        
        String motivoStr = (motivo >= 1 && motivo <= 4) ? motivos[motivo-1] : "No especificado";
        
        System.out.println("\nMotivo registrado: " + motivoStr);
        System.out.print("\n¿Está SEGURO de eliminar esta orden? (escriba 'ELIMINAR' para confirmar): ");
        String confirmacion = scanner.nextLine();
        
        if (!confirmacion.equalsIgnoreCase("ELIMINAR")) {
            System.out.println("Eliminación cancelada. La orden NO fue eliminada.");
            return;
        }
        
        // Eliminar la orden
        boolean eliminada = sistema.eliminarOrden(idOrden);
        
        if (eliminada) {
            System.out.println("Orden eliminada exitosamente.");
            
            // Registrar la eliminación en un log
            registrarLogEliminacionOrden(idOrden, motivoStr, sistema.getUsuarioActual().getNombre());
        }
    }

    private static void registrarLogEliminacionOrden(int idOrden, String motivo, String usuario) {
        try {
            java.io.FileWriter writer = new java.io.FileWriter("data/log_eliminaciones.csv", true);
            java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String fecha = sdf.format(new java.util.Date());
            
            writer.write(fecha + "," + idOrden + "," + motivo + "," + usuario + "\n");
            writer.close();
        } catch (Exception e) {
            System.out.println("⚠Error al registrar log de eliminación: " + e.getMessage());
        }
    }


    
    private static boolean mostrarMenuAdministrador(Administrador admin) {
    System.out.println("\n=== MENÚ ADMINISTRADOR ===");
    System.out.println("1. Crear tarea");
    System.out.println("2. Asignar tarea");
    System.out.println("3. Listar tareas");
    System.out.println("4. Eliminar tarea");
    System.out.println("5. Listar empleados");
    System.out.println("6. Agregar nuevo empleado");
    System.out.println("7. Eliminar empleado");
    System.out.println("8. Eliminar orden");
    System.out.println("9. Ver mi información");
    System.out.println("10. Cerrar sesión");
    System.out.print("Seleccione opción: ");
    
    int opcion = leerEntero();
    
    switch (opcion) {
        case 1:
            crearTarea(admin);
            System.out.println("\nPresione Enter para continuar...");
            scanner.nextLine();
            return false;
        case 2:
            asignarTarea(admin);
            System.out.println("\nPresione Enter para continuar...");
            scanner.nextLine();
            return false;
        case 3:
            sistema.listarTareas();
            System.out.println("\nPresione Enter para continuar...");
            scanner.nextLine();
            return false;
        case 4:
            eliminarTarea();
            System.out.println("\nPresione Enter para continuar...");
            scanner.nextLine();
            return false;
        case 5:
            listarEmpleados();
            System.out.println("\nPresione Enter para continuar...");
            scanner.nextLine();
            return false;
        case 6:
            agregarNuevoEmpleado();
            System.out.println("\nPresione Enter para continuar...");
            scanner.nextLine();
            return false;
        case 7:
            eliminarEmpleadoAdministrador(admin);
            System.out.println("\nPresione Enter para continuar...");
            scanner.nextLine();
            return false;
        case 8:
            eliminarOrdenAdministrador(admin);
            System.out.println("\nPresione Enter para continuar...");
            scanner.nextLine();
            return false;
        case 9:
            admin.mostrarInfo(false);
            System.out.println("\nPresione Enter para continuar...");
            scanner.nextLine();
            return false;
        case 10:
            sistema.setUsuarioActual(null);
            System.out.println("Sesión cerrada correctamente.");
            return true;
        default:
            System.out.println("Opción inválida");
            System.out.println("\nPresione Enter para continuar...");
            scanner.nextLine();
            return false;
    }
}
    
    private static void eliminarEmpleadoAdministrador(Administrador admin) {
    System.out.println("\n=== ELIMINAR EMPLEADO ===");
    
    // Listar solo empleados (no administradores)
    List<Empleado> empleados = sistema.getEmpleados();
    
    if (empleados.isEmpty()) {
        System.out.println("No hay empleados registrados.");
        return;
    }
    
    System.out.println("Lista de empleados:");
    for (Empleado empleado : empleados) {
        System.out.println("ID: " + empleado.getId() + 
                         " | Nombre: " + empleado.getNombre() + 
                         " | Rol: " + empleado.getRol());
    }
    
    System.out.print("\nID del empleado a eliminar (0 para cancelar): ");
    int idEmpleado = leerEntero();
    
    if (idEmpleado == 0) {
        System.out.println("Operación cancelada.");
        return;
    }
    
    // Verificar que sea un empleado (no administrador o sudo)
    Usuario usuario = sistema.buscarUsuarioPorId(idEmpleado);
    
    if (usuario == null) {
        System.out.println("❌ Usuario no encontrado.");
        return;
    }
    
    // Verificar que no sea administrador o sudo
    if (usuario instanceof Administrador || usuario instanceof Sudo) {
        System.out.println("❌ No tiene permisos para eliminar administradores.");
        System.out.println("   Solo el Sudo puede eliminar administradores.");
        return;
    }
    
    if (!(usuario instanceof Empleado)) {
        System.out.println("El usuario seleccionado no es un empleado.");
        return;
    }
    
    System.out.println("Empleado a eliminar:");
    usuario.mostrarInfo(false);
    
    System.out.print("\n¿Está seguro de eliminar este empleado? (s/n): ");
    String confirmacion = scanner.nextLine().toLowerCase();
    
    if (confirmacion.equals("s") || confirmacion.equals("si")) {
        sistema.eliminarUsuario(idEmpleado);
        System.out.println("Empleado eliminado exitosamente.");
    } else {
        System.out.println("Operación cancelada.");
    }
}
    
    private static boolean mostrarMenuCocinero(Cocinero cocinero) {
        System.out.println("\n=== MENÚ COCINERO ===");
        System.out.println("Bienvenido, " + cocinero.getNombre());
        System.out.println("Platillos preparados hoy: " + cocinero.getPlatillosPreparados());
        System.out.println("\n1. Ver mis tareas asignadas");
        System.out.println("2. Ver órdenes pendientes (resumen)");
        System.out.println("3. Ver detalles de órdenes pendientes");
        System.out.println("4. Marcar platillo como listo");
        System.out.println("5. Ver mi información");
        System.out.println("6. Cerrar sesión");
        System.out.print("Seleccione opción: ");
        
        int opcion = leerEntero();
        
        switch (opcion) {
            case 1:
                cocinero.consultarTareas();
                System.out.println("\nPresione Enter para continuar...");
                scanner.nextLine();
                return false;
            case 2:
                verResumenOrdenesPendientes();
                System.out.println("\nPresione Enter para continuar...");
                scanner.nextLine();
                return false;
            case 3:
                verOrdenesPendientes();
                System.out.println("\nPresione Enter para continuar...");
                scanner.nextLine();
                return false;
            case 4:
                marcarPlatilloListo(cocinero);
                System.out.println("\nPresione Enter para continuar...");
                scanner.nextLine();
                return false;
            case 5:
                cocinero.mostrarInfo(false);
                System.out.println("\nPresione Enter para continuar...");
                scanner.nextLine();
                return false;
            case 6:
                sistema.setUsuarioActual(null);
                System.out.println("Sesión cerrada correctamente.");
                return true;
            default:
                System.out.println("Opción inválida");
                System.out.println("\nPresione Enter para continuar...");
                scanner.nextLine();
                return false;
        }
    }
    
    private static void eliminarOrdenAdministrador(Administrador admin) {
        System.out.println("\n=== ELIMINAR ORDEN (ADMINISTRADOR) ===");
        
        // Mostrar solo órdenes activas (no entregadas)
        List<Orden> ordenesActivas = sistema.getOrdenes().stream()
            .filter(o -> !o.isEntregada())
            .collect(java.util.stream.Collectors.toList());
        
        if (ordenesActivas.isEmpty()) {
            System.out.println("No hay órdenes activas para eliminar.");
            return;
        }
        
        System.out.println("Órdenes activas (no entregadas):");
        System.out.println("=================================");
        
        for (Orden orden : ordenesActivas) {
            System.out.println("\n[Orden #" + orden.getId() + "]");
            System.out.println("Mesa: " + orden.getMesa().getNumero());
            System.out.println("Mesero: " + orden.getMesero().getNombre());
            System.out.println("Total: $" + orden.getTotal());
            System.out.println("Estado: " + (orden.estaLista() ? "✅ LISTA" : "🔄 EN PREPARACIÓN"));
            System.out.println("Progreso: " + orden.getCantidadPlatillosListos() + "/" + 
                             orden.getTotalPlatillos() + " platillos listos");
            System.out.println("-------------------");
        }
        
        System.out.print("\nID de la orden a eliminar (0 para cancelar): ");
        int idOrden = leerEntero();
        
        if (idOrden == 0) {
            System.out.println("Operación cancelada.");
            return;
        }
        
        // Verificar que la orden exista y no esté entregada
        Orden orden = sistema.buscarOrdenPorId(idOrden);
        
        if (orden == null) {
            System.out.println("❌ Orden no encontrada.");
            return;
        }
        
        if (orden.isEntregada()) {
            System.out.println("❌ No tiene permisos para eliminar órdenes ya entregadas.");
            System.out.println("   Solo el Sudo puede eliminar órdenes entregadas.");
            return;
        }
        
        // Mostrar detalles
        System.out.println("\n=== DETALLES DE LA ORDEN ===");
        orden.mostrarOrden();
        
        System.out.print("\n¿Está seguro de eliminar esta orden? (s/n): ");
        String respuesta = scanner.nextLine().toLowerCase();
        
        if (!respuesta.equals("s") && !respuesta.equals("si")) {
            System.out.println("❌ Eliminación cancelada.");
            return;
        }
        
        // Preguntar motivo breve
        System.out.print("Motivo breve de eliminación: ");
        String motivo = scanner.nextLine();
        
        // Eliminar la orden
        boolean eliminada = sistema.eliminarOrden(idOrden);
        
        if (eliminada) {
            System.out.println("✅ Orden eliminada exitosamente.");
            
            // Registrar log
            registrarLogEliminacionOrden(idOrden, "Admin: " + motivo, admin.getNombre());
            
            // Notificar al mesero si es posible
            System.out.println("📢 Informar al mesero " + orden.getMesero().getNombre() + 
                             " sobre la eliminación de la orden #" + idOrden);
        }
    }
    
    private static boolean mostrarMenuMesero(Mesero mesero) {
    System.out.println("\n=== MENÚ MESERO ===");
    System.out.println("Bienvenido, " + mesero.getNombre());
    System.out.println("1. Tomar pedido");
    System.out.println("2. Ver mis órdenes");
    System.out.println("3. Modificar orden");
    System.out.println("4. Eliminar mi orden");
    System.out.println("5. Entregar pedido");
    System.out.println("6. Ver mesas disponibles");
    System.out.println("7. Ver mis tareas");
    System.out.println("8. Ver mi información");
    System.out.println("9. Cerrar sesión");
    System.out.print("Seleccione opción: ");
    
    int opcion = leerEntero();
    
    switch (opcion) {
        case 1:
            tomarPedido(mesero);
            System.out.println("\nPresione Enter para continuar...");
            scanner.nextLine();
            return false;
        case 2:
            verOrdenesMesero(mesero);
            System.out.println("\nPresione Enter para continuar...");
            scanner.nextLine();
            return false;
        case 3:
            modificarOrden(mesero);
            System.out.println("\nPresione Enter para continuar...");
            scanner.nextLine();
            return false;
        case 4:
            eliminarOrdenMesero(mesero);
            System.out.println("\nPresione Enter para continuar...");
            scanner.nextLine();
            return false;
        case 5:
            entregarPedido(mesero);
            System.out.println("\nPresione Enter para continuar...");
            scanner.nextLine();
            return false;
        case 6:
            verMesasDisponibles();
            System.out.println("\nPresione Enter para continuar...");
            scanner.nextLine();
            return false;
        case 7:
            mesero.consultarTareas();
            System.out.println("\nPresione Enter para continuar...");
            scanner.nextLine();
            return false;
        case 8:
            mesero.mostrarInfo(false);
            System.out.println("\nPresione Enter para continuar...");
            scanner.nextLine();
            return false;
        case 9:
            sistema.setUsuarioActual(null);
            System.out.println("Sesión cerrada correctamente.");
            return true;
        default:
            System.out.println("Opción inválida");
            System.out.println("\nPresione Enter para continuar...");
            scanner.nextLine();
            return false;
    }
}
    
    private static void eliminarOrdenMesero(Mesero mesero) {
        System.out.println("\n=== ELIMINAR MI ORDEN ===");
        
        // Mostrar solo órdenes de este mesero que no estén entregadas
        List<Orden> ordenesMesero = sistema.getOrdenes().stream()
            .filter(o -> o.getMesero().getId() == mesero.getId() && !o.isEntregada())
            .collect(java.util.stream.Collectors.toList());
        
        if (ordenesMesero.isEmpty()) {
            System.out.println("No tienes órdenes activas para eliminar.");
            return;
        }
        
        System.out.println("Tus órdenes activas:");
        for (Orden orden : ordenesMesero) {
            System.out.println("\n[Orden #" + orden.getId() + "]");
            System.out.println("Mesa: " + orden.getMesa().getNumero());
            System.out.println("Total: $" + orden.getTotal());
            System.out.println("Estado: " + (orden.estaLista() ? "✅ LISTA" : "🔄 EN PREPARACIÓN"));
            System.out.println("-------------------");
        }
        
        System.out.print("\nID de tu orden a eliminar (0 para cancelar): ");
        int idOrden = leerEntero();
        
        if (idOrden == 0) {
            System.out.println("Operación cancelada.");
            return;
        }
        
        // Verificar que la orden pertenezca a este mesero
        Orden orden = sistema.buscarOrdenPorId(idOrden);
        
        if (orden == null || orden.getMesero().getId() != mesero.getId()) {
            System.out.println("❌ Esta orden no existe o no te pertenece.");
            return;
        }
        
        if (orden.isEntregada()) {
            System.out.println("❌ No puedes eliminar una orden ya entregada.");
            System.out.println("   Contacta a un administrador si hay un problema.");
            return;
        }
        
        // Verificar si hay platillos ya preparados
        if (orden.getCantidadPlatillosListos() > 0) {
            System.out.println("⚠️  Advertencia: Hay " + orden.getCantidadPlatillosListos() + 
                             " platillo(s) ya preparados.");
            System.out.print("¿Está seguro de continuar? (s/n): ");
            String respuesta = scanner.nextLine().toLowerCase();
            
            if (!respuesta.equals("s") && !respuesta.equals("si")) {
                System.out.println("❌ Eliminación cancelada.");
                return;
            }
            
            System.out.println("📢 Informar al cocinero sobre los platillos preparados que se descartarán.");
        }
        
        System.out.print("Motivo breve (ej: cliente canceló, error en pedido): ");
        String motivo = scanner.nextLine();
        
        // Confirmación final
        System.out.print("\n¿CONFIRMAR eliminación de la orden #" + idOrden + "? (s/n): ");
        String confirmacion = scanner.nextLine().toLowerCase();
        
        if (!confirmacion.equals("s") && !confirmacion.equals("si")) {
            System.out.println("❌ Eliminación cancelada.");
            return;
        }
        
        // Eliminar la orden
        boolean eliminada = sistema.eliminarOrden(idOrden);
        
        if (eliminada) {
            System.out.println("✅ Tu orden ha sido eliminada exitosamente.");
            
            // Registrar log
            registrarLogEliminacionOrden(idOrden, "Mesero: " + motivo, mesero.getNombre());
        }
    }
    
    private static void crearNuevoUsuario() {
    System.out.println("\n=== CREAR NUEVO USUARIO ===");
    System.out.println("Tipo de usuario:");
    System.out.println("1. Administrador");
    System.out.println("2. Cocinero");
    System.out.println("3. Mesero");
    System.out.print("Seleccione tipo: ");
    
    int tipo = leerEntero();
    
    System.out.print("Nombre: ");
    String nombre = scanner.nextLine();
    
    System.out.print("Email (debe terminar en .com): ");
    String email = scanner.nextLine();
    
    System.out.print("Contraseña: ");
    String password = scanner.nextLine();
    
    try {
        switch (tipo) {
            case 1:
                sistema.agregarUsuario(new Administrador(nombre, email, password));
                System.out.println("✅ Administrador creado exitosamente");
                break;
            case 2:
                sistema.agregarUsuario(new Cocinero(nombre, email, password));
                System.out.println("✅ Cocinero creado exitosamente");
                break;
            case 3:
                sistema.agregarUsuario(new Mesero(nombre, email, password));
                System.out.println("✅ Mesero creado exitosamente");
                break;
            default:
                System.out.println("❌ Tipo de usuario inválido");
                return;
        }
    } catch (EmailInvalidoException | NombreInvalidoException e) {
        System.out.println("❌ Error: " + e.getMessage());
    }
}
    
    private static void crearTarea(Administrador admin) {
    System.out.print("Título de la tarea: ");
    String titulo = scanner.nextLine();
    
    System.out.print("Descripción: ");
    String descripcion = scanner.nextLine();
    
    // Solicitar fecha con formato específico
    System.out.print("Fecha límite (Formato: yyyy-MM-dd HH:mm, ejemplo: 2024-12-31 18:30): ");
    String fechaLimite = scanner.nextLine();
    
    // Validar formato de fecha
    if (!validarFormatoFecha(fechaLimite)) {
        System.out.println("❌ Error: El formato de fecha debe ser yyyy-MM-dd HH:mm");
        System.out.println("   Ejemplo: 2024-12-31 18:30");
        return;
    }
    
    Tarea tarea = admin.crearTarea(titulo, descripcion, fechaLimite);
    sistema.agregarTarea(tarea);
    System.out.println("✅ Tarea creada exitosamente");
}

// Método para validar el formato de fecha
private static boolean validarFormatoFecha(String fecha) {
    try {
        // Intentar parsear la fecha
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        sdf.setLenient(false); // No permitir fechas inválidas como 2024-02-30
        Date fechaParseada = sdf.parse(fecha);
        return true;
    } catch (Exception e) {
        return false;
    }
}

    private static void eliminarTarea() {
    System.out.println("\n=== ELIMINAR TAREA ===");
    
    if (sistema.getTareas().isEmpty()) {
        System.out.println("No hay tareas registradas.");
        return;
    }
    
    System.out.println("Lista de tareas:");
    sistema.listarTareas();
    
    System.out.print("\nID de la tarea a eliminar (0 para cancelar): ");
    int idTarea = leerEntero();
    
    if (idTarea == 0) {
        System.out.println("Operación cancelada.");
        return;
    }
    
    Tarea tarea = sistema.buscarTareaPorId(idTarea);
    if (tarea == null) {
        System.out.println("❌ Tarea no encontrada.");
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
        System.out.println("✅ Tarea eliminada exitosamente.");
    } else {
        System.out.println("Operación cancelada.");
    }
}
    
    private static void asignarTarea(Administrador admin) {
    System.out.println("\n=== ASIGNAR TAREA ===");
    
    if (sistema.getTareas().isEmpty()) {
        System.out.println("No hay tareas disponibles para asignar.");
        System.out.println("Primero debe crear tareas.");
        return;
    }
    
    System.out.println("Tareas disponibles:");
    sistema.listarTareas();
    
    System.out.print("\nID de la tarea a asignar (0 para cancelar): ");
    int idTarea = leerEntero();
    
    if (idTarea == 0) {
        System.out.println("Operación cancelada.");
        return;
    }
    
    Tarea tarea = sistema.buscarTareaPorId(idTarea);
    if (tarea == null) {
        System.out.println("❌ Tarea no encontrada");
        return;
    }
    
    // Mostrar empleados disponibles
    List<Empleado> empleados = sistema.getEmpleados();
    if (empleados.isEmpty()) {
        System.out.println("❌ No hay empleados registrados para asignar tareas.");
        return;
    }
    
    System.out.println("\nEmpleados disponibles:");
    for (Empleado emp : empleados) {
        System.out.println("ID: " + emp.getId() + 
                         " | Nombre: " + emp.getNombre() + 
                         " | Rol: " + emp.getRol() +
                         " | Tareas asignadas: " + emp.getTareasAsignadas().size());
    }
    
    System.out.print("\nID del empleado a asignar (0 para cancelar): ");
    int idEmpleado = leerEntero();
    
    if (idEmpleado == 0) {
        System.out.println("Operación cancelada.");
        return;
    }
    
    Empleado empleado = sistema.getEmpleados().stream()
        .filter(e -> e.getId() == idEmpleado)
        .findFirst()
        .orElse(null);
    
    if (empleado != null) {
        // Si la tarea ya tenía asignado a alguien, removerla
        if (tarea.getUsuarioAsignado() != null) {
            tarea.getUsuarioAsignado().removerTarea(tarea);
            System.out.println("⚠️  Tarea reasignada. Anterior asignado: " + 
                             tarea.getUsuarioAsignado().getNombre());
        }
        
        admin.asignarTarea(tarea, empleado);
        System.out.println("✅ Tarea '" + tarea.getTitulo() + 
                         "' asignada exitosamente a " + empleado.getNombre());
    } else {
        System.out.println("❌ Empleado no encontrado");
    }
}
    
    private static void listarEmpleados() {
        List<Empleado> empleados = sistema.getEmpleados();
        if (empleados.isEmpty()) {
            System.out.println("No hay empleados registrados");
        } else {
            for (Empleado emp : empleados) {
                emp.mostrarInfo(false);
                System.out.println("-------------------");
            }
        }
    }
    
    private static void agregarNuevoEmpleado() {
    System.out.println("Tipo de empleado:");
    System.out.println("1. Cocinero");
    System.out.println("2. Mesero");
    System.out.print("Seleccione: ");
    
    int tipo = leerEntero();
    
    System.out.print("Nombre: ");
    String nombre = scanner.nextLine();
    
    System.out.print("Email (debe terminar en .com): ");
    String email = scanner.nextLine();
    
    System.out.print("Contraseña: ");
    String password = scanner.nextLine();
    
    try {
        if (tipo == 1) {
            sistema.agregarUsuario(new Cocinero(nombre, email, password));
            System.out.println("✅ Cocinero agregado exitosamente");
        } else if (tipo == 2) {
            sistema.agregarUsuario(new Mesero(nombre, email, password));
            System.out.println("✅ Mesero agregado exitosamente");
        } else {
            System.out.println("❌ Opción inválida");
        }
    } catch (EmailInvalidoException | NombreInvalidoException e) {
        System.out.println("❌ Error: " + e.getMessage());
    }
}
    
    private static void tomarPedido(Mesero mesero) {
    verMesasDisponibles();
    System.out.print("Número de mesa: ");
    int numeroMesa = leerEntero();
    
    Mesa mesa = sistema.getMesas().stream()
        .filter(m -> m.getNumero() == numeroMesa && !m.isOcupada())
        .findFirst()
        .orElse(null);
    
    if (mesa == null) {
        System.out.println("Mesa no disponible");
        return;
    }
    
    mesa.setOcupada(true);
    
    // Crear lista de ItemOrden
    List<ItemOrden> items = new ArrayList<>();
    boolean agregando = true;
    
    while (agregando) {
        System.out.println("\n=== AGREGAR PLATILLOS A LA ORDEN ===");
        System.out.println("Platillos disponibles:");
        
        for (Platillo platillo : sistema.getPlatillos()) {
            System.out.println("[ID: " + platillo.getId() + "] " + 
                             platillo.getNombre() + 
                             " - $" + platillo.getPrecio() +
                             " (" + platillo.getTiempoPreparacion() + " min)");
        }
        
        System.out.print("\nID del platillo (0 para terminar): ");
        int idPlatillo = leerEntero();
        
        if (idPlatillo == 0) {
            agregando = false;
        } else {
            Platillo platillo = sistema.getPlatillos().stream()
                .filter(p -> p.getId() == idPlatillo)
                .findFirst()
                .orElse(null);
            
            if (platillo != null) {
                System.out.print("Cantidad de '" + platillo.getNombre() + "': ");
                int cantidad = leerEntero();
                
                if (cantidad > 0) {
                    items.add(new ItemOrden(platillo, cantidad));
                    System.out.println("✅ Agregados " + cantidad + " x '" + platillo.getNombre() + "' a la orden");
                } else {
                    System.out.println("❌ La cantidad debe ser mayor a 0");
                }
            } else {
                System.out.println("❌ Platillo no encontrado");
            }
        }
    }
    
    // Verificar si se agregaron items
    if (!items.isEmpty()) {
    Orden orden = mesero.tomarPedido(mesa, items);
    sistema.agregarOrden(orden); // Usar este método en lugar de add directo
    System.out.println("\n✅ Pedido registrado exitosamente");
    System.out.println("Orden #" + orden.getId());
    System.out.println("Total: $" + orden.getTotal());
    sistema.agregarVenta(orden.getTotal());
    
    // Mostrar resumen de la orden
    orden.mostrarOrden();
}
      else {
        System.out.println("❌ No se agregaron platillos a la orden");
        mesa.setOcupada(false); // Liberar la mesa
    }
}
    
    private static void verOrdenesPendientes() {
        List<Orden> ordenesPendientes = sistema.getOrdenes().stream()
            .filter(o -> !o.isEntregada())
            .collect(java.util.stream.Collectors.toList());
        
        if (ordenesPendientes.isEmpty()) {
            System.out.println("No hay órdenes pendientes");
        } else {
            for (Orden orden : ordenesPendientes) {
                orden.mostrarOrden();
                System.out.println("===================");
            }
        }
    }
    
    private static void verResumenOrdenesPendientes() {
    List<Orden> ordenesPendientes = sistema.getOrdenes().stream()
        .filter(o -> !o.isEntregada())
        .collect(java.util.stream.Collectors.toList());
    
    if (ordenesPendientes.isEmpty()) {
        System.out.println("✅ No hay órdenes pendientes.");
        return;
    }
    
    System.out.println("\n=== RESUMEN DE ÓRDENES PENDIENTES ===");
    System.out.println("Total órdenes: " + ordenesPendientes.size());
    
    for (Orden orden : ordenesPendientes) {
        int totalPlatillos = orden.getTotalPlatillos();
        int listos = orden.getCantidadPlatillosListos(); // Cambiado
        int pendientes = orden.getCantidadPlatillosPendientes(); // Cambiado
        
        String estado = orden.estaLista() ? "✅ LISTA" : "🔄 EN PROCESO";
        
        System.out.println("\nOrden #" + orden.getId() + 
                         " | Mesa: " + orden.getMesa().getNumero() +
                         " | " + estado +
                         " | Platillos: " + listos + "/" + totalPlatillos);
        
        if (pendientes > 0) {
            System.out.print("  Platillos pendientes: ");
            List<ItemOrden> itemsPendientes = orden.getItemsPendientes();
            for (ItemOrden item : itemsPendientes) {
                System.out.print(item.getPlatillo().getNombre() + 
                               " x" + item.getCantidadPendiente() + " ");
            }
            System.out.println();
        }
    }
}
    
    private static void verOrdenesMesero(Mesero mesero) {
    List<Orden> ordenesMesero = sistema.getOrdenes().stream()
        .filter(o -> o.getMesero().getId() == mesero.getId() && !o.isEntregada())
        .collect(java.util.stream.Collectors.toList());
    
    if (ordenesMesero.isEmpty()) {
        System.out.println("No tienes órdenes activas");
    } else {
        System.out.println("\n=== MIS ÓRDENES ACTIVAS ===");
        System.out.println("Total órdenes: " + ordenesMesero.size());
        
        for (Orden orden : ordenesMesero) {
            System.out.println("\n[Orden #" + orden.getId() + "]");
            System.out.println("Mesa: " + orden.getMesa().getNumero());
            
            // Mostrar estado de la orden
            if (orden.estaLista()) {
                System.out.println("Estado: ✅ LISTA PARA ENTREGAR");
            } else {
                int total = orden.getTotalPlatillos();
                int listos = orden.getCantidadPlatillosListos(); // Cambiado
                System.out.println("Estado: 🔄 EN PREPARACIÓN (" + listos + "/" + total + " platillos listos)");
            }
            
            // Mostrar items con su estado
            System.out.println("Items:");
            for (ItemOrden item : orden.getItems()) {
                System.out.println("  - " + item.getPlatillo().getNombre() + 
                                 " x" + item.getCantidad() + 
                                 " | $" + item.getPlatillo().getPrecio() + " c/u" +
                                 " | Estado: " + item.getCantidadLista() + "/" + item.getCantidad() + " listos");
            }
            
            System.out.println("Total: $" + orden.getTotal());
            System.out.println("-------------------");
        }
    }
}
    
    private static void marcarPlatilloListo(Cocinero cocinero) {
    // Mostrar órdenes pendientes
    List<Orden> ordenesPendientes = sistema.getOrdenes().stream()
        .filter(o -> !o.isEntregada())
        .collect(java.util.stream.Collectors.toList());
    
    if (ordenesPendientes.isEmpty()) {
        System.out.println("No hay órdenes pendientes para preparar.");
        return;
    }
    
    System.out.println("\n=== ÓRDENES PENDIENTES ===");
    for (Orden orden : ordenesPendientes) {
        System.out.println("\n[Orden #" + orden.getId() + "]");
        System.out.println("Mesa: " + orden.getMesa().getNumero());
        System.out.println("Mesero: " + orden.getMesero().getNombre());
        System.out.println("Progreso: " + orden.getCantidadPlatillosListos() + "/" + orden.getTotalPlatillos() + " platillos listos");
        System.out.println("-------------------");
    }
    
    System.out.print("\nID de la orden a trabajar: ");
    int idOrden = leerEntero();
    
    Orden orden = sistema.getOrdenes().stream()
        .filter(o -> o.getId() == idOrden && !o.isEntregada())
        .findFirst()
        .orElse(null);
    
    if (orden == null) {
        System.out.println("❌ Orden no encontrada o ya entregada.");
        return;
    }
    
    // Mostrar items de esta orden específica
    System.out.println("\n=== ITEMS DE LA ORDEN #" + orden.getId() + " ===");
    orden.mostrarParaCocinero();
    
    List<ItemOrden> itemsPendientes = orden.getItemsPendientes();
    
    if (itemsPendientes.isEmpty()) {
        System.out.println("\n✅ ¡Todos los items de esta orden ya están completos!");
        System.out.println("Estado: LISTA PARA ENTREGAR");
        return;
    }
    
    System.out.println("\nSeleccione el platillo a marcar como listo:");
    System.out.println("-------------------------------------------");
    
    int index = 1;
    for (ItemOrden item : itemsPendientes) {
        System.out.println(index + ". [ID: " + item.getPlatillo().getId() + "] " + 
                         item.getPlatillo().getNombre() + 
                         " - Pendientes: " + item.getCantidadPendiente() + "/" + item.getCantidad());
        index++;
    }
    
    System.out.print("\nSeleccione número del platillo (0 para cancelar): ");
    int seleccion = leerEntero();
    
    if (seleccion == 0) {
        System.out.println("Operación cancelada.");
        return;
    }
    
    if (seleccion < 1 || seleccion > itemsPendientes.size()) {
        System.out.println("❌ Selección inválida.");
        return;
    }
    
    ItemOrden itemSeleccionado = itemsPendientes.get(seleccion - 1);
    
    // Preguntar cuántas unidades marcar como listas
    System.out.println("\nPlatillo seleccionado: " + itemSeleccionado.getPlatillo().getNombre());
    System.out.println("Cantidad pendiente: " + itemSeleccionado.getCantidadPendiente() + " de " + itemSeleccionado.getCantidad());
    System.out.print("¿Cuántas unidades marcar como listas? (1-" + itemSeleccionado.getCantidadPendiente() + "): ");
    int cantidad = leerEntero();
    
    if (cantidad < 1 || cantidad > itemSeleccionado.getCantidadPendiente()) {
        System.out.println("❌ Cantidad inválida.");
        return;
    }
    
    // Usar el nuevo método que maneja cantidad
    cocinero.marcarComidaLista(orden, itemSeleccionado.getPlatillo(), cantidad);
    
    System.out.println("\n✅ Marcadas " + cantidad + " unidad(es) de '" + 
                     itemSeleccionado.getPlatillo().getNombre() + "' como LISTAS.");
    
    // Verificar si todos los items están completos
    if (orden.estaLista()) {
        System.out.println("\n🎉 ¡¡¡TODOS LOS ITEMS DE LA ORDEN #" + 
                         orden.getId() + " ESTÁN COMPLETOS!!!");
        System.out.println("📢 Informar al mesero que la orden está lista para entregar.");
        System.out.println("Mesa: " + orden.getMesa().getNumero());
        System.out.println("Mesero asignado: " + orden.getMesero().getNombre());
    } else {
        int pendientes = orden.getCantidadPlatillosPendientes();
        int total = orden.getTotalPlatillos();
        System.out.println("🔄 Progreso: " + (total - pendientes) + "/" + 
                         total + " platillos listos");
        System.out.println("⏳ Aún faltan " + pendientes + " platillo(s) por preparar.");
    }
    
    // Guardar estado del sistema
    sistema.guardarEstado();
}
    
    private static void entregarPedido(Mesero mesero) {
    verOrdenesMesero(mesero);
    System.out.print("ID de la orden a entregar: ");
    int idOrden = leerEntero();
    
    Orden orden = sistema.getOrdenes().stream()
        .filter(o -> o.getId() == idOrden && o.getMesero().getId() == mesero.getId())
        .findFirst()
        .orElse(null);
    
    if (orden != null) {
        if (!orden.estaLista()) {
            System.out.println("⚠️  Esta orden no está completamente lista.");
            System.out.println("Platillos pendientes: " + 
                             orden.getCantidadPlatillosPendientes() + "/" + 
                             orden.getTotalPlatillos());
            System.out.print("¿Desea continuar con la entrega? (s/n): ");
            String respuesta = scanner.nextLine().toLowerCase();
            if (!respuesta.equals("s") && !respuesta.equals("si")) {
                System.out.println("Entrega cancelada.");
                return;
            }
        }
        
        // Llama al método entregarPedido del mesero
        mesero.entregarPedido(orden);
        
        // Liberar la mesa si la orden se entregó
        if (orden.isEntregada()) {
            orden.getMesa().setOcupada(false);
            System.out.println("✅ Mesa " + orden.getMesa().getNumero() + " liberada.");
        }
    } else {
        System.out.println("Orden no encontrada o no pertenece a este mesero");
    }
    
    // Llama al método entregarPedido del mesero
mesero.entregarPedido(orden);

// Liberar la mesa si la orden se entregó
if (orden.isEntregada()) {
    orden.getMesa().setOcupada(false);
    System.out.println("✅ Mesa " + orden.getMesa().getNumero() + " liberada.");
    // Guardar estado
    sistema.guardarEstado();
}
}

    private static void modificarOrden(Mesero mesero) {
    System.out.println("\n=== MODIFICAR ORDEN ===");
    
    // Mostrar órdenes del mesero que no estén entregadas
    List<Orden> ordenesMesero = sistema.getOrdenes().stream()
        .filter(o -> o.getMesero().getId() == mesero.getId() && !o.isEntregada())
        .collect(java.util.stream.Collectors.toList());
    
    if (ordenesMesero.isEmpty()) {
        System.out.println("No tienes órdenes activas para modificar.");
        return;
    }
    
    System.out.println("Tus órdenes activas:");
    for (Orden orden : ordenesMesero) {
        System.out.println("\n[Orden #" + orden.getId() + "]");
        System.out.println("Mesa: " + orden.getMesa().getNumero());
        System.out.println("Estado: " + (orden.estaLista() ? "✅ LISTA" : "🔄 EN PREPARACIÓN"));
        System.out.println("Total: $" + orden.getTotal());
    }
    
    System.out.print("\nID de la orden a modificar (0 para cancelar): ");
    int idOrden = leerEntero();
    
    if (idOrden == 0) {
        System.out.println("Operación cancelada.");
        return;
    }
    
    Orden orden = sistema.getOrdenes().stream()
        .filter(o -> o.getId() == idOrden && o.getMesero().getId() == mesero.getId())
        .findFirst()
        .orElse(null);
    
    if (orden == null) {
        System.out.println("❌ Orden no encontrada o no pertenece a este mesero.");
        return;
    }
    
    // Mostrar menú de modificación
    boolean modificando = true;
    while (modificando) {
        System.out.println("\n=== MODIFICANDO ORDEN #" + orden.getId() + " ===");
        System.out.println("Mesa: " + orden.getMesa().getNumero());
        orden.mostrarOrden();
        
        System.out.println("\nOpciones de modificación:");
        System.out.println("1. Agregar platillo a la orden");
        System.out.println("2. Eliminar platillo de la orden");
        System.out.println("3. Modificar cantidad de un platillo");
        System.out.println("4. Ver estado actual de la orden");
        System.out.println("5. Finalizar modificación");
        System.out.print("Seleccione opción: ");
        
        int opcion = leerEntero();
        
        switch (opcion) {
            case 1:
                agregarPlatilloAOrden(orden);
                break;
            case 2:
                eliminarPlatilloDeOrden(orden);
                break;
            case 3:
                modificarCantidadEnOrden(orden);
                break;
            case 4:
                orden.mostrarOrden();
                System.out.println("\nPresione Enter para continuar...");
                scanner.nextLine();
                break;
            case 5:
                modificando = false;
                System.out.println("✅ Modificación de orden finalizada.");
                // Guardar cambios
                sistema.guardarEstado();
                break;
            default:
                System.out.println("❌ Opción inválida.");
        }
    }
}

private static void agregarPlatilloAOrden(Orden orden) {
    System.out.println("\n=== AGREGAR PLATILLO A LA ORDEN ===");
    System.out.println("Platillos disponibles:");
    
    for (Platillo platillo : sistema.getPlatillos()) {
        System.out.println("[ID: " + platillo.getId() + "] " + 
                         platillo.getNombre() + 
                         " - $" + platillo.getPrecio() +
                         " (" + platillo.getTiempoPreparacion() + " min)");
    }
    
    System.out.print("\nID del platillo a agregar (0 para cancelar): ");
    int idPlatillo = leerEntero();
    
    if (idPlatillo == 0) {
        System.out.println("Operación cancelada.");
        return;
    }
    
    Platillo platillo = sistema.getPlatillos().stream()
        .filter(p -> p.getId() == idPlatillo)
        .findFirst()
        .orElse(null);
    
    if (platillo == null) {
        System.out.println("❌ Platillo no encontrado.");
        return;
    }
    
    System.out.print("Cantidad de '" + platillo.getNombre() + "': ");
    int cantidad = leerEntero();
    
    if (cantidad <= 0) {
        System.out.println("❌ La cantidad debe ser mayor a 0.");
        return;
    }
    
    // Verificar si ya existe en la orden
    boolean yaExiste = false;
    for (ItemOrden item : orden.getItems()) {
        if (item.getPlatillo().getId() == idPlatillo) {
            yaExiste = true;
            System.out.println("⚠️  Este platillo ya existe en la orden.");
            System.out.println("Cantidad actual: " + item.getCantidad());
            System.out.print("¿Desea agregar " + cantidad + " más? (s/n): ");
            String respuesta = scanner.nextLine().toLowerCase();
            
            if (respuesta.equals("s") || respuesta.equals("si")) {
                // Agregar a la cantidad existente
                orden.agregarCantidadPlatillo(idPlatillo, cantidad);
                System.out.println("✅ Agregados " + cantidad + " más de '" + platillo.getNombre() + "'.");
            } else {
                System.out.println("Operación cancelada.");
            }
            break;
        }
    }
    
    if (!yaExiste) {
        orden.agregarNuevoPlatillo(platillo, cantidad);
        System.out.println("✅ Agregados " + cantidad + " x '" + platillo.getNombre() + "' a la orden.");
    }
}

private static void eliminarPlatilloDeOrden(Orden orden) {
    System.out.println("\n=== ELIMINAR PLATILLO DE LA ORDEN ===");
    System.out.println("Platillos en la orden:");
    
    List<ItemOrden> items = orden.getItems();
    if (items.isEmpty()) {
        System.out.println("La orden no tiene platillos.");
        return;
    }
    
    int index = 1;
    for (ItemOrden item : items) {
        System.out.println(index + ". [ID: " + item.getPlatillo().getId() + "] " + 
                         item.getPlatillo().getNombre() + 
                         " x" + item.getCantidad() + 
                         " - $" + item.getSubtotal() +
                         " (" + item.getCantidadLista() + "/" + item.getCantidad() + " listos)");
        index++;
    }
    
    System.out.print("\nNúmero del platillo a eliminar (0 para cancelar): ");
    int seleccion = leerEntero();
    
    if (seleccion == 0) {
        System.out.println("Operación cancelada.");
        return;
    }
    
    if (seleccion < 1 || seleccion > items.size()) {
        System.out.println("❌ Selección inválida.");
        return;
    }
    
    ItemOrden itemSeleccionado = items.get(seleccion - 1);
    
    System.out.println("\nPlatillo seleccionado: " + itemSeleccionado.getPlatillo().getNombre());
    System.out.println("Cantidad: " + itemSeleccionado.getCantidad());
    System.out.println("Listos: " + itemSeleccionado.getCantidadLista() + "/" + itemSeleccionado.getCantidad());
    System.out.print("¿Está seguro de eliminar este platillo de la orden? (s/n): ");
    String confirmacion = scanner.nextLine().toLowerCase();
    
    if (confirmacion.equals("s") || confirmacion.equals("si")) {
        boolean eliminado = orden.eliminarPlatillo(itemSeleccionado.getPlatillo().getId());
        if (eliminado) {
            System.out.println("✅ Platillo eliminado de la orden.");
            
            // Si había platillos listos, informar al cocinero
            if (itemSeleccionado.getCantidadLista() > 0) {
                System.out.println("⚠️  Se eliminaron " + itemSeleccionado.getCantidadLista() + 
                                 " platillo(s) que ya estaban listos.");
                System.out.println("   Informar al cocinero sobre el cambio.");
            }
        } else {
            System.out.println("❌ Error al eliminar el platillo.");
        }
    } else {
        System.out.println("Operación cancelada.");
    }
}

private static void modificarCantidadEnOrden(Orden orden) {
    System.out.println("\n=== MODIFICAR CANTIDAD DE PLATILLO ===");
    System.out.println("Platillos en la orden:");
    
    List<ItemOrden> items = orden.getItems();
    if (items.isEmpty()) {
        System.out.println("La orden no tiene platillos.");
        return;
    }
    
    int index = 1;
    for (ItemOrden item : items) {
        System.out.println(index + ". [ID: " + item.getPlatillo().getId() + "] " + 
                         item.getPlatillo().getNombre() + 
                         " - Cantidad actual: " + item.getCantidad() +
                         " (" + item.getCantidadLista() + " listos)");
        index++;
    }
    
    System.out.print("\nNúmero del platillo a modificar (0 para cancelar): ");
    int seleccion = leerEntero();
    
    if (seleccion == 0) {
        System.out.println("Operación cancelada.");
        return;
    }
    
    if (seleccion < 1 || seleccion > items.size()) {
        System.out.println("❌ Selección inválida.");
        return;
    }
    
    ItemOrden itemSeleccionado = items.get(seleccion - 1);
    
    System.out.println("\nPlatillo seleccionado: " + itemSeleccionado.getPlatillo().getNombre());
    System.out.println("Cantidad actual: " + itemSeleccionado.getCantidad());
    System.out.println("Listos actualmente: " + itemSeleccionado.getCantidadLista());
    System.out.print("Nueva cantidad (0 para eliminar): ");
    int nuevaCantidad = leerEntero();
    
    if (nuevaCantidad < 0) {
        System.out.println("❌ La cantidad no puede ser negativa.");
        return;
    }
    
    if (nuevaCantidad == itemSeleccionado.getCantidad()) {
        System.out.println("⚠️  La cantidad es la misma. No se realizaron cambios.");
        return;
    }
    
    if (nuevaCantidad == 0) {
        // Eliminar el platillo
        System.out.print("¿Eliminar este platillo de la orden? (s/n): ");
        String confirmacion = scanner.nextLine().toLowerCase();
        if (confirmacion.equals("s") || confirmacion.equals("si")) {
            orden.eliminarPlatillo(itemSeleccionado.getPlatillo().getId());
            System.out.println("✅ Platillo eliminado.");
        } else {
            System.out.println("Operación cancelada.");
        }
        return;
    }
    
    // Verificar si la nueva cantidad es menor que la cantidad ya lista
    if (nuevaCantidad < itemSeleccionado.getCantidadLista()) {
        System.out.println("⚠️  Advertencia: La nueva cantidad (" + nuevaCantidad + 
                         ") es menor que la cantidad ya lista (" + 
                         itemSeleccionado.getCantidadLista() + ").");
        System.out.println("   Los platillos listos se ajustarán a " + nuevaCantidad + ".");
        
        System.out.print("¿Continuar? (s/n): ");
        String confirmacion = scanner.nextLine().toLowerCase();
        if (!confirmacion.equals("s") && !confirmacion.equals("si")) {
            System.out.println("Operación cancelada.");
            return;
        }
    }
    
    // Modificar la cantidad
    boolean modificado = orden.modificarCantidadPlatillo(
        itemSeleccionado.getPlatillo().getId(), 
        nuevaCantidad
    );
    
    if (modificado) {
        System.out.println("✅ Cantidad modificada a " + nuevaCantidad + ".");
        
        // Actualizar referencia al item modificado
        for (ItemOrden item : orden.getItems()) {
            if (item.getPlatillo().getId() == itemSeleccionado.getPlatillo().getId()) {
                System.out.println("   Listos actualizados: " + item.getCantidadLista() + "/" + item.getCantidad());
                break;
            }
        }
    } else {
        System.out.println("❌ Error al modificar la cantidad.");
    }
}
    
    private static void verMesasDisponibles() {
        System.out.println("Mesas disponibles:");
        for (Mesa mesa : sistema.getMesas()) {
            if (!mesa.isOcupada()) {
                System.out.println("Mesa #" + mesa.getNumero() + " (Capacidad: " + mesa.getCapacidad() + ")");
            }
        }
    }
    
    private static int leerEntero() {
        while (true) {
            try {
                return Integer.parseInt(scanner.nextLine());
            } catch (NumberFormatException e) {
                System.out.print("Ingrese un número válido: ");
            }
        }
    }
}
