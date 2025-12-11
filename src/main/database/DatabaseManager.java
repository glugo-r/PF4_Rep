package database;

import usuarios.*;
import tareas.EstadoTarea;
import tareas.Tarea;
import restaurante.*;
import java.io.*;
import java.util.*;
import java.text.SimpleDateFormat;

public class DatabaseManager 
{
    private static final String USUARIOS_FILE = "data/usuarios.csv";
    private static final String TAREAS_FILE = "data/tareas.csv";
    private static final String PLATILLOS_FILE = "data/platillos.csv";
    private static final String VENTAS_FILE = "data/ventas.csv";
    private static final String ORDENES_FILE = "data/ordenes.csv";
    private static final String MESAS_FILE = "data/mesas.csv";
    private static final String ESTADISTICAS_FILE = "data/estadisticas.csv";
        
    // Formato de fecha para las órdenes
    private static final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    
    // ========== USUARIOS ==========
    public static void guardarUsuarios(List<Usuario> usuarios) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(USUARIOS_FILE))) {
            writer.println("id,nombre,email,rol,password");
            for (Usuario usuario : usuarios) {
                writer.println(usuario.getId() + "," + 
                              usuario.getNombre() + "," + 
                              usuario.getEmail() + "," + 
                              usuario.getRol() + "," +
                              usuario.getPassword());
            }
        } catch (IOException e) {
            System.out.println("Error al guardar usuarios: " + e.getMessage());
        }
    }
    
    public static List<Usuario> cargarUsuarios() {
        List<Usuario> usuarios = new ArrayList<>();
        File file = new File(USUARIOS_FILE);
        
        if (!file.exists()) {
            // Crear Sudo por defecto
            usuarios.add(new Sudo(null, null));
            guardarUsuarios(usuarios);
            return usuarios;
        }
        
        try (BufferedReader reader = new BufferedReader(new FileReader(USUARIOS_FILE))) {
            String line;
            boolean firstLine = true;
            
            while ((line = reader.readLine()) != null) {
                if (firstLine) {
                    firstLine = false;
                    continue;
                }
                
                String[] datos = line.split(",");
                if (datos.length >= 5) {
                    switch (datos[3]) {
                        case "Sudo":
                            usuarios.add(new Sudo(null, null));
                            break;
                        case "Administrador":
                            usuarios.add(new Administrador(datos[1], datos[2], datos[4], null, null));
                            break;
                        case "Cocinero":
                            usuarios.add(new Cocinero(datos[1], datos[2], datos[4], null, null));
                            break;
                        case "Mesero":
                            usuarios.add(new Mesero(datos[1], datos[2], datos[4], null, null));
                            break;
                    }
                }
            }
        } catch (IOException e) {
            System.out.println("Error al cargar usuarios: " + e.getMessage());
        }
        
        return usuarios;
    }
    
    // ========== PLATILLOS ==========
    public static void guardarPlatillos(List<Platillo> platillos) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(PLATILLOS_FILE))) {
            writer.println("id,nombre,descripcion,precio,tiempoPreparacion,categoria");
            for (Platillo platillo : platillos) {
                writer.println(platillo.getId() + "," +
                              platillo.getNombre() + "," +
                              platillo.getDescripcion() + "," +
                              platillo.getPrecio() + "," +
                              platillo.getTiempoPreparacion() + "," +
                              platillo.getCategoria());
            }
        } catch (IOException e) {
            System.out.println("Error al guardar platillos: " + e.getMessage());
        }
    }
    
    public static List<Platillo> cargarPlatillos() {
        List<Platillo> platillos = new ArrayList<>();
        File file = new File(PLATILLOS_FILE);
        
        if (!file.exists()) {
            return platillos; // Retorna lista vacía
        }
        
        try (BufferedReader reader = new BufferedReader(new FileReader(PLATILLOS_FILE))) {
            String line;
            boolean firstLine = true;
            
            while ((line = reader.readLine()) != null) {
                if (firstLine) {
                    firstLine = false;
                    continue;
                }
                
                String[] datos = line.split(",");
                if (datos.length >= 6) {
                    try {
                        int id = Integer.parseInt(datos[0]);
                        String nombre = datos[1];
                        String descripcion = datos[2];
                        double precio = Double.parseDouble(datos[3]);
                        int tiempoPreparacion = Integer.parseInt(datos[4]);
                        String categoria = datos[5];
                        
                        // Crear platillo y forzar el ID
                        Platillo platillo = new Platillo(nombre, descripcion, precio, tiempoPreparacion, categoria);
                        // Necesitamos un método para setear el ID, o modificar el constructor
                        // Por ahora, crearemos uno nuevo con los datos
                        platillos.add(platillo);
                    } catch (NumberFormatException e) {
                        System.out.println("Error en formato de datos de platillo: " + line);
                    }
                }
            }
        } catch (IOException e) {
            System.out.println("Error al cargar platillos: " + e.getMessage());
        }
        
        return platillos;
    }
    
    // ========== MESAS ==========
    public static void guardarMesas(List<Mesa> mesas) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(MESAS_FILE))) {
            writer.println("numero,capacidad,ocupada");
            for (Mesa mesa : mesas) {
                writer.println(mesa.getNumero() + "," +
                              mesa.getCapacidad() + "," +
                              mesa.isOcupada());
            }
        } catch (IOException e) {
            System.out.println("Error al guardar mesas: " + e.getMessage());
        }
    }
    
    public static List<Mesa> cargarMesas() {
        List<Mesa> mesas = new ArrayList<>();
        File file = new File(MESAS_FILE);
        
        if (!file.exists()) {
            // Crear 10 mesas por defecto
            for (int i = 1; i <= 10; i++) {
                mesas.add(new Mesa(i, 4));
            }
            guardarMesas(mesas);
            return mesas;
        }
        
        try (BufferedReader reader = new BufferedReader(new FileReader(MESAS_FILE))) {
            String line;
            boolean firstLine = true;
            
            while ((line = reader.readLine()) != null) {
                if (firstLine) {
                    firstLine = false;
                    continue;
                }
                
                String[] datos = line.split(",");
                if (datos.length >= 3) {
                    try {
                        int numero = Integer.parseInt(datos[0]);
                        int capacidad = Integer.parseInt(datos[1]);
                        boolean ocupada = Boolean.parseBoolean(datos[2]);
                        
                        Mesa mesa = new Mesa(numero, capacidad);
                        mesa.setOcupada(ocupada);
                        mesas.add(mesa);
                    } catch (NumberFormatException e) {
                        System.out.println("Error en formato de datos de mesa: " + line);
                    }
                }
            }
        } catch (IOException e) {
            System.out.println("Error al cargar mesas: " + e.getMessage());
        }
        
        return mesas;
    }
    
    // ========== ÓRDENES ==========
    public static void guardarOrdenes(List<Orden> ordenes) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(ORDENES_FILE))) {
            writer.println("id,mesa,mesero_id,fecha,total,entregada");
            for (Orden orden : ordenes) {
                String fechaStr = sdf.format(orden.getFecha());
                writer.println(orden.getId() + "," +
                              orden.getMesa().getNumero() + "," +
                              orden.getMesero().getId() + "," +
                              fechaStr + "," +
                              orden.getTotal() + "," +
                              orden.isEntregada());
                
                // También guardar los items de la orden en archivo separado
                guardarItemsOrden(orden);
            }
        } catch (IOException e) {
            System.out.println("Error al guardar órdenes: " + e.getMessage());
        }
    }
    
    private static void guardarItemsOrden(Orden orden) {
        try (PrintWriter writer = new PrintWriter(new FileWriter("data/orden_" + orden.getId() + "_items.csv"))) {
            writer.println("platillo_id,cantidad,cantidad_lista");
            for (ItemOrden item : orden.getItems()) {
                writer.println(item.getPlatillo().getId() + "," +
                              item.getCantidad() + "," +
                              item.getCantidadLista());
            }
        } catch (IOException e) {
            System.out.println("Error al guardar items de orden: " + e.getMessage());
        }
    }
    
    public static List<Orden> cargarOrdenes(List<Mesa> mesas, List<Usuario> usuarios, List<Platillo> platillos) {
        List<Orden> ordenes = new ArrayList<>();
        File file = new File(ORDENES_FILE);
        
        if (!file.exists()) {
            return ordenes; // Retorna lista vacía
        }
        
        try (BufferedReader reader = new BufferedReader(new FileReader(ORDENES_FILE))) {
            String line;
            boolean firstLine = true;
            
            while ((line = reader.readLine()) != null) {
                if (firstLine) {
                    firstLine = false;
                    continue;
                }
                
                String[] datos = line.split(",");
                if (datos.length >= 6) {
                    try {
                        int id = Integer.parseInt(datos[0]);
                        int numeroMesa = Integer.parseInt(datos[1]);
                        int meseroId = Integer.parseInt(datos[2]);
                        String fechaStr = datos[3];
                        double total = Double.parseDouble(datos[4]);
                        boolean entregada = Boolean.parseBoolean(datos[5]);
                        
                        // Buscar mesa
                        Mesa mesa = null;
                        for (Mesa m : mesas) {
                            if (m.getNumero() == numeroMesa) {
                                mesa = m;
                                break;
                            }
                        }
                        
                        if (mesa == null) {
                            System.out.println("Mesa no encontrada para orden #" + id);
                            continue;
                        }
                        
                        // Buscar mesero
                        Mesero mesero = null;
                        for (Usuario usuario : usuarios) {
                            if (usuario instanceof Mesero && usuario.getId() == meseroId) {
                                mesero = (Mesero) usuario;
                                break;
                            }
                        }
                        
                        if (mesero == null) {
                            System.out.println("Mesero no encontrado para orden #" + id);
                            continue;
                        }
                        
                        // Crear orden
                        Orden orden = new Orden(mesa, mesero);
                        // Forzar ID y otros datos
                        orden.setEntregada(entregada);
                        
                        // Cargar items de la orden
                        cargarItemsOrden(orden, platillos, id);
                        
                        ordenes.add(orden);
                        
                    } catch (Exception e) {
                        System.out.println("Error al cargar orden: " + line + " - " + e.getMessage());
                    }
                }
            }
        } catch (IOException e) {
            System.out.println("Error al cargar órdenes: " + e.getMessage());
        }
        
        return ordenes;
    }
    
    private static void cargarItemsOrden(Orden orden, List<Platillo> platillos, int ordenId) {
        File file = new File("data/orden_" + ordenId + "_items.csv");
        
        if (!file.exists()) {
            return;
        }
        
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            boolean firstLine = true;
            
            while ((line = reader.readLine()) != null) {
                if (firstLine) {
                    firstLine = false;
                    continue;
                }
                
                String[] datos = line.split(",");
                if (datos.length >= 3) {
                    try {
                        int platilloId = Integer.parseInt(datos[0]);
                        int cantidad = Integer.parseInt(datos[1]);
                        int cantidadLista = Integer.parseInt(datos[2]);
                        
                        // Buscar platillo
                        Platillo platillo = null;
                        for (Platillo p : platillos) {
                            if (p.getId() == platilloId) {
                                platillo = p;
                                break;
                            }
                        }
                        
                        if (platillo != null) {
                            ItemOrden item = new ItemOrden(platillo, cantidad);
                            // Marcar las unidades que ya están listas
                            for (int i = 0; i < cantidadLista; i++) {
                                item.marcarListo();
                            }
                            orden.agregarItem(item);
                        }
                    } catch (NumberFormatException e) {
                        System.out.println("Error en formato de item: " + line);
                    }
                }
            }
        } catch (IOException e) {
            System.out.println("Error al cargar items de orden: " + e.getMessage());
        }
    }
    
    // ========== VENTAS ==========
    public static void registrarVenta(double monto, String detalles) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(VENTAS_FILE, true))) {
            writer.println(new Date() + "," + monto + "," + detalles);
        } catch (IOException e) {
            System.out.println("Error al registrar venta: " + e.getMessage());
        }
    }
    
    // ========== ESTADÍSTICAS ==========
    public static void guardarEstadisticasCocinero(Cocinero cocinero) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(ESTADISTICAS_FILE, true))) {
            Date ahora = new Date();
            writer.println(ahora + "," + 
                          cocinero.getId() + "," + 
                          cocinero.getNombre() + "," + 
                          cocinero.getPlatillosPreparados());
        } catch (IOException e) {
            System.out.println("Error al guardar estadísticas: " + e.getMessage());
        }
    }
    
    // Método para cargar estadísticas del día
    public static int cargarPlatillosPreparadosHoy(int cocineroId) {
        int total = 0;
        File file = new File(ESTADISTICAS_FILE);
        
        if (!file.exists()) {
            return 0;
        }
        
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String hoy = dateFormat.format(new Date());
        
        try (BufferedReader reader = new BufferedReader(new FileReader(ESTADISTICAS_FILE))) {
            String line;
            
            while ((line = reader.readLine()) != null) {
                String[] datos = line.split(",");
                if (datos.length >= 4) {
                    try {
                        String fecha = datos[0];
                        int id = Integer.parseInt(datos[1]);
                        int platillos = Integer.parseInt(datos[3]);
                        
                        // Verificar si es del cocinero y de hoy
                        if (id == cocineroId && fecha.contains(hoy)) {
                            total += platillos;
                        }
                    } catch (NumberFormatException e) {
                        // Ignorar líneas con formato incorrecto
                    }
                }
            }
        } catch (IOException e) {
            System.out.println("Error al cargar estadísticas: " + e.getMessage());
        }
        
        return total;
    }
    
    // ========== TAREAS ==========
    public static void guardarTareas(List<Tarea> tareas) 
    {
        try (PrintWriter writer = new PrintWriter(new FileWriter(TAREAS_FILE))) {
            writer.println("id,titulo,descripcion,fechaLimite,estado,usuarioAsignadoId");
            for (Tarea tarea : tareas) {
                int usuarioId = (tarea.getUsuarioAsignado() != null) ? tarea.getUsuarioAsignado().getId() : -1;
                writer.println(tarea.getId() + "," +
                               tarea.getTitulo() + "," +
                               tarea.getDescripcion() + "," +
                               tarea.getFechaLimite() + "," +
                               tarea.getEstado() + "," +
                               usuarioId);
            }
        } catch (IOException e) {
            System.out.println("Error al guardar tareas: " + e.getMessage());
        }
    }

    public static List<Tarea> cargarTareas(List<Usuario> usuarios) {
        List<Tarea> tareas = new ArrayList<>();
        File file = new File(TAREAS_FILE);

        if (!file.exists()) {
            return tareas; // No hay tareas aún
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(TAREAS_FILE))) {
            String line;
            boolean firstLine = true;

            while ((line = reader.readLine()) != null) {
                if (firstLine) {
                    firstLine = false;
                    continue; // Saltar encabezado
                }

                String[] datos = line.split(",");
                if (datos.length >= 6) {
                    int id = Integer.parseInt(datos[0]);
                    String titulo = datos[1];
                    String descripcion = datos[2];
                    String fechaLimite = datos[3];
                    EstadoTarea estado = EstadoTarea.valueOf(datos[4]);
                    int usuarioId = Integer.parseInt(datos[5]);

                    // Usar el nuevo constructor
                    Tarea tarea = new Tarea(id, titulo, descripcion, fechaLimite, estado);

                    // Vincular usuario asignado si existe
                    Usuario asignado = usuarios.stream()
                                               .filter(u -> u.getId() == usuarioId)
                                               .findFirst()
                                               .orElse(null);
                    if (asignado instanceof Empleado) {
                        tarea.setUsuarioAsignado((Empleado) asignado);
                    }

                    tareas.add(tarea);
                }
            }
        } catch (IOException e) {
            System.out.println("Error al cargar tareas: " + e.getMessage());
        }

        return tareas;
    }
    
}
