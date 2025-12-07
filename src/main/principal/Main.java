package principal;
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
            System.out.println("\n Guardando estado del sistema...");
            sistema.guardarEstado();
            notificador.detener();
            System.out.println(" Sistema cerrado correctamente.");
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
            
            if (usuario instanceof Sudo)
                cerrarSesion = ((Sudo) usuario).mostrarMenu();
            else if (usuario instanceof Administrador) {
                cerrarSesion = ((Administrador) usuario).mostrarMenu();
            } else if (usuario instanceof Cocinero) {
                cerrarSesion = ((Cocinero) usuario).mostrarMenu();
            } else if (usuario instanceof Mesero) {
                cerrarSesion = ((Mesero) usuario).mostrarMenu();
            } else {
                System.out.println("Rol no reconocido");
                cerrarSesion = true;
            }
        }
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
            System.out.println("  Esta orden no está completamente lista.");
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
            System.out.println(" Mesa " + orden.getMesa().getNumero() + " liberada.");
        }
    } else {
        System.out.println("Orden no encontrada o no pertenece a este mesero");
    }
    
    // Llama al método entregarPedido del mesero
mesero.entregarPedido(orden);

// Liberar la mesa si la orden se entregó
if (orden.isEntregada()) {
    orden.getMesa().setOcupada(false);
    System.out.println(" Mesa " + orden.getMesa().getNumero() + " liberada.");
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
        System.out.println(" Orden no encontrada o no pertenece a este mesero.");
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
                System.out.println(" Modificación de orden finalizada.");
                // Guardar cambios
                sistema.guardarEstado();
                break;
            default:
                System.out.println(" Opción inválida.");
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
            System.out.println("  Este platillo ya existe en la orden.");
            System.out.println("Cantidad actual: " + item.getCantidad());
            System.out.print("¿Desea agregar " + cantidad + " más? (s/n): ");
            String respuesta = scanner.nextLine().toLowerCase();
            
            if (respuesta.equals("s") || respuesta.equals("si")) {
                // Agregar a la cantidad existente
                orden.agregarCantidadPlatillo(idPlatillo, cantidad);
                System.out.println(" Agregados " + cantidad + " más de '" + platillo.getNombre() + "'.");
            } else {
                System.out.println("Operación cancelada.");
            }
            break;
        }
    }
    
    if (!yaExiste) {
        orden.agregarNuevoPlatillo(platillo, cantidad);
        System.out.println(" Agregados " + cantidad + " x '" + platillo.getNombre() + "' a la orden.");
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
        System.out.println(" Selección inválida.");
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
            System.out.println(" Platillo eliminado de la orden.");
            
            // Si había platillos listos, informar al cocinero
            if (itemSeleccionado.getCantidadLista() > 0) {
                System.out.println("  Se eliminaron " + itemSeleccionado.getCantidadLista() + 
                                 " platillo(s) que ya estaban listos.");
                System.out.println("   Informar al cocinero sobre el cambio.");
            }
        } else {
            System.out.println(" Error al eliminar el platillo.");
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
        System.out.println("Error al modificar la cantidad.");
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
